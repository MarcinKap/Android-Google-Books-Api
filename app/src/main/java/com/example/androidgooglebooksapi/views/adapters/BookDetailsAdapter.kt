package com.example.androidgooglebooksapi.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidgooglebooksapi.R
import com.example.androidgooglebooksapi.models.bookDetails.AdditionalInformation

class BookDetailsAdapter(var additionalInformationsList: ArrayList<AdditionalInformation>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.adapter_book_details, parent, false)
        return (CustomViewHolder(view))
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val additionalInformation = additionalInformationsList.get(position)
        (holder as CustomViewHolder).bind(additionalInformation.title, additionalInformation.text)
    }
    override fun getItemCount(): Int {
        return additionalInformationsList.size
    }
    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //create section book list adapter
        fun bind(title: String, text: String) {
            val additionalInformationTitle: TextView =
                itemView.findViewById(R.id.additional_information_title)
            additionalInformationTitle.setText(title)
            val additionalInformationText: TextView =
                itemView.findViewById(R.id.additional_information_text)
            additionalInformationText.setText(text)
        }
    }





}