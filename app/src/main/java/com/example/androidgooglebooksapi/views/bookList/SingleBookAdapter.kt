package com.example.androidgooglebooksapi.views.bookList


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import androidx.recyclerview.widget.RecyclerView
import com.example.androidgooglebooksapi.R
import com.example.androidgooglebooksapi.models.bookList.Items
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide


class SingleBookAdapter(var booksList: List<Items>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

//    private var freeBooksList: ArrayList<Items> = TODO()
//    private var paidBooksList: ArrayList<Items>

    var freeBooksList: ArrayList<Items> = ArrayList()
    var paidBooksList: ArrayList<Items> = ArrayList()

    init {


        booksList.forEach {
            if ("FREE".equals(it.saleInfo.saleability)) {
                freeBooksList.add(it)
            } else {
                paidBooksList.add(it)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        if (viewType == 0) {
            //Create section with title (Free/Paid books)
            val view: View = inflater.inflate(R.layout.adapter_section_book, parent, false)
            return BookSectionViewHolder(view)

        } else {
            //Create item with book
            val view: View = inflater.inflate(R.layout.adapter_single_book, parent, false)
            return SingleBookViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val singleBook = booksList[position]

        if (singleBook == null) {
            return;
        }

        //Now check wich ViewHolder should be run
        if(holder.itemViewType == 0){
                if(position < freeBooksList.size+1 && freeBooksList.size!=0){
                    (holder as BookSectionViewHolder).bind("Free book list")

                }else{
                    (holder as BookSectionViewHolder).bind("Paid book list")
                }

        }else{
            (holder as SingleBookViewHolder).bind(singleBook)
        }

        holder.setIsRecyclable(false);


    }

    override fun getItemCount(): Int {
        return booksList.size
    }


    //Definition of view type (Section or Item)
    override fun getItemViewType(position: Int): Int {
        if (position == 0 || (freeBooksList.size!=0 && position == freeBooksList.size + 1) ) {
            return 0; //Section
        } else {
            return 1; //Item
        }
//
//        return super.getItemViewType(position)
    }

    class BookSectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //create section book list adapter
        fun bind(title: String) {
            var sectionTitle: TextView = itemView.findViewById(R.id.sectionTitle)
            sectionTitle.setText(title)
        }

    }

    class SingleBookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        //create single book adapter
        fun bind(singleBook: Items) {
            var bookImage: ImageView = itemView.findViewById(R.id.image_book)
            val bookTitle: TextView = itemView.findViewById(R.id.book_title_text_view)
            bookTitle.setText(singleBook.volumeInfo.title)

            if (singleBook.volumeInfo != null && singleBook.volumeInfo.imageLinks != null && singleBook.volumeInfo.imageLinks.smallThumbnail != null) {
                Glide
                    .with(itemView)
                    .load(singleBook.volumeInfo.imageLinks.smallThumbnail)
                    .thumbnail(Glide.with(itemView.context).load(R.drawable.loading_apple))
                    .error(R.drawable.no_photo)
                    .fitCenter()
                    .into(bookImage);
            } else {
                bookImage.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                bookImage.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                bookImage.setBackgroundResource(R.drawable.no_photo)
                bookImage.updateLayoutParams<ConstraintLayout.LayoutParams> { verticalBias = 0.5f }
            }

        }


    }





}


