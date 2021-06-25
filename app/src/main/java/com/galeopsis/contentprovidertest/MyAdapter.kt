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
import com.galeopsis.contentprovidertest.contacts.model.Contacts
import java.util.*
import kotlin.collections.ArrayList

class MyAdapter(private val context: Context, private val contactsList: ArrayList<Contacts>) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>(), Filterable {

    private val searchList = ArrayList<Contacts>(contactsList)
    private var search = ArrayList<Contacts>()
    private var onCallListener: OnCallListener<Contacts>? = null

    fun setListener(onCallListener: OnCallListener<Contacts>) {
        this.onCallListener = onCallListener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(contacts: Contacts) {
            val tvName = itemView.findViewById<TextView>(R.id.tvName)
            val tvNumber = itemView.findViewById<TextView>(R.id.tvNumber)
            tvName.text = contacts.name
            tvNumber.text = contacts.number
        }
    }

    init {
        search = contactsList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recycleview_row, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return search.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = contactsList[position]
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

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filteredList = ArrayList<Contacts>()

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

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                contactsList.clear()
                contactsList.addAll(results!!.values as List<Contacts>)
                notifyDataSetChanged()
                /* contactList.clear()
                 search = results?.values as ArrayList<Contact>
                 notifyDataSetChanged()*/
            }
        }
    }
}



