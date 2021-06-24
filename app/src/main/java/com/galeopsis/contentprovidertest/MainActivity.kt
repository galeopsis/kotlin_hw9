package com.galeopsis.contentprovidertest

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.galeopsis.contentprovidertest.contacts.listener.OnCallListener
import com.galeopsis.contentprovidertest.contacts.listener.Utility
import com.galeopsis.contentprovidertest.contacts.model.Contact
import com.galeopsis.contentprovidertest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnCallListener<Contact> {

    private lateinit var binding: ActivityMainBinding
    private val contacts = ArrayList<Contact>()
    private val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    lateinit var contact: RecyclerView

    override fun onCall(t: Contact) {
        Utility.makeCall(this, t.number)
    }

    override fun onMessage(t: Contact) {
        Utility.doMessage(this, t.number)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val searchIcon = binding.contactSearch.findViewById<ImageView>(R.id.search_mag_icon)
        searchIcon.setColorFilter(Color.WHITE)

        val cancelIcon = binding.contactSearch.findViewById<ImageView>(R.id.search_close_btn)
        cancelIcon.setColorFilter(Color.WHITE)

        val textView = binding.contactSearch.findViewById<TextView>(R.id.search_src_text)
        textView.setTextColor(Color.WHITE)

        contact = findViewById(R.id.contactItem)
        contact.layoutManager = LinearLayoutManager(contact.context)
        contact.setHasFixedSize(true)
        binding.contactSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                MyAdapter(getContacts(), this@MainActivity).filter.filter(newText)
                return false
            }
        })

        loadContacts()
    }

    private fun loadContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
            || checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.CALL_PHONE
                ), PERMISSIONS_REQUEST_READ_CONTACTS
            )
        } else {
            binding.contactItem.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            val adapter = MyAdapter(getContacts(), this)
            binding.contactItem.adapter = adapter
            adapter.setListener(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts()
            } else {
                showToast("Вы должны дать разрешение на доступ контактам.")
            }
        }
    }

    private fun getContacts(): ArrayList<Contact> {

        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        if (cursor != null) {
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val id =
                        cursor.let { cursor.getString(it.getColumnIndex(ContactsContract.Contacts._ID)) }
                    val name =
                        cursor.let { cursor.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)) }
                    val phoneNumber =
                        (cursor.let { cursor.getString(it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) }).toInt()

                    if (phoneNumber > 0) {
                        val cursorPhone = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                            arrayOf(id),
                            null
                        )

                        if (cursorPhone != null) {
                            if (cursorPhone.count > 0) {
                                while (cursorPhone.moveToNext()) {
                                    val phoneNumValue = cursorPhone.let {
                                        cursorPhone.getString(
                                            it.getColumnIndex(
                                                ContactsContract.CommonDataKinds.Phone.NUMBER
                                            )
                                        )
                                    }
                                    contacts.add(Contact(name, phoneNumValue))
                                }
                            }
                        }
                        cursorPhone?.close()
                    }
                }
            } else {
                showToast("Нет доступных контактов")
            }
        }
        cursor?.close()
        return contacts
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
