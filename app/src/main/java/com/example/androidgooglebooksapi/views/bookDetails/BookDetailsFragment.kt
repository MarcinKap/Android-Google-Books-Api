package com.example.androidgooglebooksapi.views.bookDetails

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidgooglebooksapi.R
import com.example.androidgooglebooksapi.models.bookDetails.AdditionalInformation
import com.example.androidgooglebooksapi.models.bookList.Items
import com.example.androidgooglebooksapi.views.bookList.BookListAdapter

class BookDetailsFragment : Fragment()  {

    companion object {
        lateinit var singleBook : Items

        fun newInstance(item : Items): BookDetailsFragment {
            val args = Bundle()
            args.putSerializable("item", item)
            val fragment = BookDetailsFragment()
            fragment.arguments = args
            return fragment
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        singleBook = arguments?.get("item") as Items

        val itemView = inflater.inflate(R.layout.fragment_book_details, container, false)
        val recyclerView: RecyclerView = itemView.findViewById(R.id.book_details_additional_informations_recycler_view)
        val textViewTitle : TextView = itemView.findViewById(R.id.title_book_details)
        val textViewDescriptionHeader : TextView = itemView.findViewById(R.id.book_details_description_header)
        val textViewDescriptionText : TextView = itemView.findViewById(R.id.book_details_description)
        val textViewAdditionalInformationHeader : TextView = itemView.findViewById(R.id.book_details_additional_informations_header)
        val bookImage: ImageView = itemView.findViewById(R.id.image_view_book_details)

        textViewTitle.setText(singleBook.volumeInfo.title)
        textViewAdditionalInformationHeader.setText(resources.getString(R.string.additional_informations))


        if(singleBook.volumeInfo.description != null){
            textViewDescriptionHeader.setText(resources.getString(R.string.description))
            textViewDescriptionText.setText(singleBook.volumeInfo.description)
        }else{
            textViewDescriptionHeader.visibility = View.GONE
            textViewDescriptionText.visibility = View.GONE
        }




        //Download image
        if (singleBook.volumeInfo != null && singleBook.volumeInfo.imageLinks != null && singleBook.volumeInfo.imageLinks.smallThumbnail != null) {
            Glide
                .with(itemView)
                .load(singleBook.volumeInfo.imageLinks.smallThumbnail)
                .thumbnail(Glide.with(itemView.context).load(R.drawable.loading_apple))
                .error(R.drawable.no_photo)
                .fitCenter()
                .into(bookImage);

        } else {
            bookImage.setImageDrawable(null)
            bookImage.setBackgroundResource(R.drawable.no_photo)
            bookImage.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            bookImage.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;

            bookImage.updateLayoutParams<ConstraintLayout.LayoutParams> { verticalBias = 0.5f }
        }


        val additionalInformationsList = ArrayList<AdditionalInformation>()

        if(singleBook.volumeInfo.authors != null)
            additionalInformationsList.add(AdditionalInformation(resources.getString(R.string.author) , singleBook.volumeInfo.authors.get(0)))
        if(singleBook.volumeInfo.publisher != null)
            additionalInformationsList.add(AdditionalInformation(resources.getString(R.string.publisher) , singleBook.volumeInfo.publisher))
        if(singleBook.volumeInfo.publishedDate != null)
            additionalInformationsList.add(AdditionalInformation(resources.getString(R.string.published_on) , singleBook.volumeInfo.publishedDate))
        if(singleBook.volumeInfo.pageCount.toString() != null && singleBook.volumeInfo.pageCount != 0)
            additionalInformationsList.add(AdditionalInformation(resources.getString(R.string.pages) , singleBook.volumeInfo.pageCount.toString()))

        recyclerView.adapter = BookDetailsAdapter(additionalInformationsList)

        var spanCount = 1
        if (context?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 2
        }

        recyclerView.layoutManager = GridLayoutManager(context, spanCount)

        return itemView;
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("singleBook", singleBook)
        outState.putString("currentFragment", "booksDetailsFragment")
        super.onSaveInstanceState(outState)
    }



}