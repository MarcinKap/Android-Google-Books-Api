package com.example.androidgooglebooksapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.fragment.app.FragmentManager
import com.example.androidgooglebooksapi.models.bookList.Items
import com.example.androidgooglebooksapi.views.fragments.BookDetailsFragment
import com.example.androidgooglebooksapi.views.fragments.BooksListFragment

class MainActivity : AppCompatActivity() {

    companion object{
        var currentPositionOnMainList = 0
        var currentPositionToShowOnSmallList= 0
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            MainActivity.currentPositionOnMainList =
                savedInstanceState.getInt("currentPositionOnList", 0)
            MainActivity.currentPositionToShowOnSmallList =
                savedInstanceState.getInt("currentPositionToShow", 0)

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
                BooksListFragment.newInstance()
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

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt("currentPositionToShowOnSmallList", currentPositionToShowOnSmallList)
        outState.putInt("currentPositionOnMainList", currentPositionOnMainList)
    }
}