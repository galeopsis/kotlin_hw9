package com.galeopsis.contentprovidertest

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.galeopsis.contentprovidertest.contacts.listener.OnCallListener
import com.galeopsis.contentprovidertest.contacts.model.Contact
import java.util.*
import kotlin.collections.ArrayList

class MyAdapter(private val contactList: ArrayList<Contact>, private val context: Context) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>(), Filterable {

    private val searchList = ArrayList<Contact>(contactList)
    private var search = ArrayList<Contact>()

    private var onCallListener: OnCallListener<Contact>? = null

    fun setListener(onCallListener: OnCallListener<Contact>) {
        this.onCallListener = onCallListener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(contact: Contact) {
            val tvName = itemView.findViewById<TextView>(R.id.tvName)
            val tvNumber = itemView.findViewById<TextView>(R.id.tvNumber)
            tvName.text = contact.name
            tvNumber.text = contact.number
        }
    }

    init {
        search = contactList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.recycleview_row, parent, false)
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = contactList[position]
        holder.bindItems(currentItem)

        holder.itemView.findViewById<ImageButton>(R.id.ibCall).setOnClickListener {
            if (onCallListener != null) {
                onCallListener!!.onCall(currentItem)
            }
        }

        holder.itemView.findViewById<ImageButton>(R.id.ibMessage).setOnClickListener {
            if (onCallListener != null) {
                onCallListener!!.onMessage(currentItem)
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra("super_key", search[position].toString())
            context.startActivity(intent)
            Log.d("Selected:", search[position].toString())
        }
    }

    override fun getItemCount(): Int {
        return search.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filteredList = ArrayList<Contact>()

                if (constraint.isBlank() or constraint.isEmpty()) {
                    filteredList.addAll(searchList)
                } else {
                    val filterPattern = constraint.toString().lowercase(Locale.ROOT).trim()

                    searchList.forEach {
                        if (it.name.lowercase(Locale.ROOT).contains(filterPattern)) {
                            filteredList.add(it)
                        }
                    }
                }

                val result = FilterResults()
                result.values = filteredList

                return result
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                contactList.clear()
                contactList.addAll(results!!.values as List<Contact>)
                notifyDataSetChanged()
            }
        }
    }
}



