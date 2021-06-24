package com.galeopsis.contentprovidertest.contacts.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.galeopsis.contentprovidertest.R
import com.galeopsis.contentprovidertest.contacts.listener.OnCallListener
import com.galeopsis.contentprovidertest.contacts.model.Contact
import java.util.*
import kotlin.collections.ArrayList

class MyAdapter(private val contactList: ArrayList<Contact>) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>(), Filterable {

    private var onCallListener: OnCallListener<Contact>? = null

    var contactFilterList = ArrayList<Contact>()

    fun setListener(onCallListener: OnCallListener<Contact>) {
        this.onCallListener = onCallListener
    }

    init {
        contactFilterList = contactList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contactList[position]
        holder.bindItems(contact)
        holder.itemView.findViewById<ImageButton>(R.id.ibCall).setOnClickListener {
            if (onCallListener != null) {
                onCallListener!!.onCall(contact)
            }
        }
        holder.itemView.findViewById<ImageButton>(R.id.ibMessage).setOnClickListener {
            if (onCallListener != null) {
                onCallListener!!.onMessage(contact)
            }
        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(contact: Contact) {
            val tvName = itemView.findViewById<TextView>(R.id.tvName)
            val tvNumber = itemView.findViewById<TextView>(R.id.tvNumber)
            tvName.text = contact.name
            tvNumber.text = contact.number
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    contactFilterList = contactList
                } else {
                    val resultList = ArrayList<Contact>()
                    for (row in contactList) {
                        if (row.name.lowercase(Locale.ROOT)
                                .contains(charSearch.lowercase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    contactFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = contactFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                contactFilterList = results?.values as ArrayList<Contact>
                notifyDataSetChanged()
            }
        }
    }
}