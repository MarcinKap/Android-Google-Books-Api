package com.example.androidgooglebooksapi.views.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.androidgooglebooksapi.MainActivity
import com.example.androidgooglebooksapi.R
import com.example.androidgooglebooksapi.models.bookList.Items
import com.example.androidgooglebooksapi.views.BaseFragment
import com.example.androidgooglebooksapi.views.adapters.BookPagerAdapter

class BookPagerFragment : BaseFragment() {
    private var viewPager: ViewPager2? = null
    private lateinit var newItemsList: ArrayList<Items>
    private lateinit var freeBookList: ArrayList<Items>
    private lateinit var paidBookList: ArrayList<Items>

    companion object {
        fun newInstance(
            newItemsList: ArrayList<Items>,
            freeBookList: ArrayList<Items>,
            paidBookList: ArrayList<Items>
        ): BookPagerFragment {
            val args = Bundle()
            args.putSerializable("newItemList", newItemsList)
            args.putSerializable("freeBookList", freeBookList)
            args.putSerializable("paidBookList", paidBookList)
            val fragment = BookPagerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        newItemsList = arguments?.get("newItemList") as ArrayList<Items>
        freeBookList = arguments?.get("freeBookList") as ArrayList<Items>
        paidBookList = arguments?.get("paidBookList") as ArrayList<Items>

        viewPager = inflater.inflate(R.layout.fragment_book_pager, container, false) as ViewPager2?
        viewPager?.adapter = BookPagerAdapter(this, newItemsList)

        // Set the current position and add a listener that will update the selection coordinator when
        // paging the images.
        MainActivity.currentPositionToShow =
            getPosition(MainActivity.currentPositionOnList, freeBookList, paidBookList)
        viewPager?.currentItem = MainActivity.currentPositionToShow
        viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                MainActivity.currentPositionOnList = position
                super.onPageSelected(position)
            }
        })

        prepareSharedElementTransition()

        // Avoid a postponeEnterTransition on orientation change, and postpone only of first creation.
        if (savedInstanceState == null) {
            postponeEnterTransition()
        }
        return viewPager
    }

    fun getPosition(
        position: Int,
        freeBooksList: ArrayList<Items>,
        paidBookList: ArrayList<Items>
    ): Int {
        if (position <= freeBooksList.size && freeBooksList.size != 0 || paidBookList.size ==0) {
            return position - 1
        } else if (position >= freeBooksList.size + 1 && freeBooksList.size != 0) {
            return position - 2
        } else {
            return position - 1
        }
    }

    /**
     * Prepares the shared element transition from and back to the grid fragment.
     */
    private fun prepareSharedElementTransition() {
        val transition = TransitionInflater
            .from(context)
            .inflateTransition(R.transition.image_shared_element_transition)

        sharedElementEnterTransition = transition

        // A similar mapping is set at the GridFragment with a setExitSharedElementCallback.
        setEnterSharedElementCallback(
            object : SharedElementCallback() {
                override fun onMapSharedElements(
                    names: List<String>,
                    sharedElements: MutableMap<String, View>
                ) {
                    // Locate the image view at the primary fragment (the ImageFragment that is currently
                    // visible). To locate the fragment, call instantiateItem with the selection position.
                    // At this stage, the method will simply return the fragment at the position and will
                    // not create a new one.

                    val currentFragment: Fragment? =
                        (view?.context as AppCompatActivity).supportFragmentManager.findFragmentById(
                            MainActivity.currentPositionOnList
                        )
                    val view2: View? = currentFragment?.view
                    if (view2 == null) {
                        return;
                    }

                    // Map the first shared element name to the child ImageView.
//                    sharedElements[names[0]] = view2.findViewById(R.id.image_book)!!
                    sharedElements[names[0]] = view2.findViewById(R.id.image_book)!!
                }
            })
    }


}