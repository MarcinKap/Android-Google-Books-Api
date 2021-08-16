package com.example.androidgooglebooksapi.views.bookList


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import androidx.recyclerview.widget.RecyclerView
import com.example.androidgooglebooksapi.R
import com.example.androidgooglebooksapi.models.bookList.Items
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.example.androidgooglebooksapi.views.bookDetails.BookDetailsFragment
import com.google.android.material.transition.MaterialFadeThrough


class BookListAdapter(var booksList: List<Items>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val VIEW_TYPE_SECTION: Int = 0
    val VIEW_TYPE_ITEM = 1

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

        if (viewType == 0) { //Create section with title (Free/Paid books)
            val view: View = inflater.inflate(R.layout.adapter_section_book, parent, false)
            return BookSectionViewHolder(view)

        } else { //Create item with book
            val view: View = inflater.inflate(R.layout.adapter_single_book, parent, false)
            return SingleBookViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //Now check wich ViewHolder should be run
        if (holder.itemViewType == VIEW_TYPE_SECTION) {
            if (position < freeBooksList.size + 1 && freeBooksList.size != 0) {
                (holder as BookSectionViewHolder).bind("Free book list")
            } else {
                (holder as BookSectionViewHolder).bind("Paid book list")
            }
        } else {
            var singleBook = getSingleBook(position)
            if (singleBook == null) {
                return;
            }
            (holder as SingleBookViewHolder).bind(singleBook)
        }
    }

    fun getSingleBook(position: Int): Items {
        if (position < freeBooksList.size + 1 && freeBooksList.size != 0) {
            return freeBooksList[position - 1]
        } else if (position >= freeBooksList.size + 1 && freeBooksList.size != 0) {
            return paidBooksList[position - freeBooksList.size - 2]
        } else {
            return paidBooksList[position - 1]
        }
    }


    override fun getItemCount(): Int {
        if (freeBooksList.size == 0 || freeBooksList.size == 0) {
            return booksList?.size + 1
        } else {
            return booksList.size + 2
        }
    }


    //Definition of view type (Section or Item)
    override fun getItemViewType(position: Int): Int {
        if (position == 0 || (freeBooksList.size != 0 && position == freeBooksList.size + 1)) {
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
            val bookContainer: ConstraintLayout = itemView.findViewById(R.id.single_book)

            //Set book title
            bookTitle.setText(singleBook.volumeInfo.title)

            //Download image
            if (singleBook.volumeInfo != null && singleBook.volumeInfo.imageLinks != null && singleBook.volumeInfo.imageLinks.thumbnail != null) {
                bookImage.setImageDrawable(null)
                bookImage.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                bookImage.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                Glide
                    .with(itemView)
                    .load(singleBook.volumeInfo.imageLinks.thumbnail)
                    .thumbnail(Glide.with(itemView.context).load(R.drawable.loading_apple))
                    .error(R.drawable.no_photo)
                    .fitCenter()
                    .into(bookImage);
                bookImage.invalidate()

            } else {
                bookImage.setImageDrawable(null)
                bookImage.setBackgroundResource(R.drawable.no_photo)
                bookImage.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                bookImage.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;

                bookImage.updateLayoutParams<ConstraintLayout.LayoutParams> { verticalBias = 0.5f }
            }


            val customListener : CustomListener = CustomListener(singleBook)

            //Set on clicklistener to open details
            bookContainer.setOnClickListener(
                customListener
            )


        }


    }

    class CustomListener
    /**
     * Empty constructor
     */
    internal constructor(item : Items) : View.OnClickListener {

        var singleBook = item

        override fun onClick(v: View) {


            (v.context as AppCompatActivity).supportFragmentManager
                .beginTransaction().replace(
                    R.id.container_fragment,
                    BookDetailsFragment.newInstance(singleBook)
                ).addToBackStack(null).commit()


        }
    }


}


