package com.example.androidgooglebooksapi.views.bookList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidgooglebooksapi.R
import com.example.androidgooglebooksapi.models.bookList.BookList
import android.os.Build

import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup


class BooksListFragment(val booklist: BookList?) : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_book_list, container, false)

        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerview_books_list)


        val gridLayout = GridLayoutManager(activity, 2)
        gridLayout.spanSizeLookup = object : SpanSizeLookup() {

            override fun getSpanSize(position: Int): Int {

                var freeBookListSize = 0

                booklist?.items?.forEach{
                    if("FREE".equals(it.saleInfo.saleability)){
                        freeBookListSize++
                    }
                }

                if (position == 0 || (freeBookListSize!=0 && position == freeBookListSize+1 ))
                {
                    return 2;
                } else {
                    return 1;
                }
            }
        }

        recyclerView.apply {
            layoutManager = gridLayout
        }
        if (booklist != null) {
            recyclerView.adapter = SingleBookAdapter(booklist.items)
        }


        val editText : EditText = view.findViewById(R.id.edit_text_books_title);


        return view;
    }



}