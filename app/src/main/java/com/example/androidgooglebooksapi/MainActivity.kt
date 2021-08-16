package com.example.androidgooglebooksapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.androidgooglebooksapi.models.bookList.Items
import com.example.androidgooglebooksapi.views.bookDetails.BookDetailsFragment
import com.example.androidgooglebooksapi.views.bookList.BooksListFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            if("booksListFragment".equals(savedInstanceState.getString("currentFragment"))){
                savedInstanceState.getString("booksTitle")?.let { addBookListFragment(it) }
            }else if("booksDetailsFragment".equals(savedInstanceState.getString("currentFragment"))){
                addBookDetailsFragment(savedInstanceState.getSerializable("singleBook") as Items)
            }
        }else{
            addBookListFragment()
        }
    }

    private fun addBookListFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.container_fragment,
                BooksListFragment.newInstance()
            )
            .commit()
    }

    private fun addBookListFragment(searchingValue : String) {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.container_fragment,
                BooksListFragment.newInstance(searchingValue)
            )
            .commit()
    }

    private fun addBookDetailsFragment(items: Items) {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.container_fragment,
                BookDetailsFragment.newInstance(items)
            )
            .commit()
    }


    override fun onBackPressed() {
        val fm: FragmentManager = supportFragmentManager
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack()
        } else {
            super.onBackPressed()
        }
    }


}