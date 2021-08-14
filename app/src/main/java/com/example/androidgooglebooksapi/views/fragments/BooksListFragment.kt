package com.example.androidgooglebooksapi.views.fragments

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
import com.example.androidgooglebooksapi.views.adapters.SingleBookAdapter

class BooksListFragment(val booklist: BookList?) : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {





        val view = inflater.inflate(R.layout.fragment_book_list, container, false)

        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerview_books_list)

        recyclerView.apply {
            layoutManager = GridLayoutManager(activity, 2)
        }

        if (booklist != null) {
            recyclerView.adapter = SingleBookAdapter(booklist.items)
        }

//        recyclerView.setNestedScrollingEnabled(false);


        val editText : EditText = view.findViewById(R.id.edit_text_books_title);


        return view;
    }



}