package com.example.androidgooglebooksapi.views.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.androidgooglebooksapi.models.bookList.Items
import com.example.androidgooglebooksapi.views.fragments.BookDetailsFragment

class BookPagerAdapter(fragment: Fragment, bookList : ArrayList<Items>) : FragmentStateAdapter(fragment) {

    private val fragment = fragment
    private val bookList = bookList


    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun createFragment(position: Int): Fragment {
        return BookDetailsFragment.newInstance(bookList[position])
    }



}