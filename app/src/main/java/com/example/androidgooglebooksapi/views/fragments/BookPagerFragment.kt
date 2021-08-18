package com.example.androidgooglebooksapi.views.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
        MainActivity.currentPositionToShowOnSmallList =
            getPositionOnSmallList(
                MainActivity.currentPositionOnMainList,
                freeBookList,
                paidBookList
            )

        viewPager!!.setCurrentItem(MainActivity.currentPositionToShowOnSmallList, false)
        viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                MainActivity.currentPositionToShowOnSmallList = position
                MainActivity.currentPositionOnMainList = getPositionOnMainList(
                    MainActivity.currentPositionToShowOnSmallList,
                    freeBookList,
                    paidBookList
                )



                super.onPageSelected(position)
            }
        })
        prepareEnterSharedElementTransition()

        // Avoid a postponeEnterTransition on orientation change, and postpone only of first creation.
        if (savedInstanceState == null) {
            postponeEnterTransition()
        }
        return viewPager
    }

    fun getPositionOnSmallList(
        position: Int,
        freeBooksList: ArrayList<Items>,
        paidBookList: ArrayList<Items>
    ): Int {
        if (position <= freeBooksList.size && freeBooksList.size != 0) {
            return position - 1
        } else if (position >= freeBooksList.size + 1 && freeBooksList.size != 0) {
            return position - 2
        } else {
            return position - 1
        }
    }

    fun getPositionOnMainList(
        positionOnSmallList: Int,
        freeBooksList: ArrayList<Items>,
        paidBookList: ArrayList<Items>
    ): Int {

        if (positionOnSmallList < freeBooksList.size && freeBooksList.size != 0) {
            return positionOnSmallList + 1
        } else if (positionOnSmallList >= freeBooksList.size && freeBooksList.size != 0) {
            return positionOnSmallList + 2
        } else {
            return positionOnSmallList + 1
        }
    }

    /**
     * Prepares the shared element transition from and back to the grid fragment.
     */
    private fun prepareEnterSharedElementTransition() {
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
                            MainActivity.currentPositionOnMainList
                        )
                    val view2: View? = currentFragment?.view
                    if (view2 == null) {
                        return;
                    }

                    val image : ImageView = view2.findViewById(R.id.image_book)
                    sharedElements[names[0]] = image
                }
            })
    }





}