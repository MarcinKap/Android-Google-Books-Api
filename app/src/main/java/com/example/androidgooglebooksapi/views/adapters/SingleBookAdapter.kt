package com.example.androidgooglebooksapi.views.adapters


import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.Gravity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout

import androidx.recyclerview.widget.RecyclerView
import com.example.androidgooglebooksapi.R
import com.example.androidgooglebooksapi.models.bookList.Items
import com.squareup.picasso.Picasso
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.solver.state.State
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide


class SingleBookAdapter(val booksList: List<Items>) :
    RecyclerView.Adapter<SingleBookAdapter.SingleBookViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleBookViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.adapter_single_book, parent, false)
        return SingleBookViewHolder(view)
    }

    override fun onBindViewHolder(holder: SingleBookViewHolder, position: Int) {
        val singleBook = booksList[position]

        if (singleBook == null) {
            return;
        }

        var bookImage: ImageView = holder.itemView.findViewById(R.id.image_book)
        val bookTitle: TextView = holder.itemView.findViewById(R.id.book_title_text_view)
        bookTitle.setText(singleBook.volumeInfo.title)


        if (singleBook.volumeInfo != null && singleBook.volumeInfo.imageLinks != null && singleBook.volumeInfo.imageLinks.smallThumbnail != null) {
            Glide
                .with(holder.view)
                .load(singleBook.volumeInfo.imageLinks.smallThumbnail)
                .thumbnail(Glide.with(holder.view.context).load(R.drawable.loading_apple))
                .error(R.drawable.no_photo)
                .fitCenter()
                .into(bookImage);

        } else {
            bookImage.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            bookImage.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            bookImage.setBackgroundResource(R.drawable.no_photo)
            bookImage.updateLayoutParams<ConstraintLayout.LayoutParams> { verticalBias = 0.5f }


        }

        holder.setIsRecyclable(false);


    }

    override fun getItemCount(): Int {
        return booksList.size
    }


    class SingleBookViewHolder(val view: View) : RecyclerView.ViewHolder(view) {


    }


}


