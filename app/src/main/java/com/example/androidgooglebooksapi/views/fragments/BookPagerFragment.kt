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
import android.app.Activity
import android.view.inputmethod.InputMethodManager


class BookPagerFragment : BaseFragment() {


    companion object {
        private var viewPager: ViewPager2? = null
        private var freeItemsListSize: Int = 0
        private var paidItemsListSize: Int = 0
        private lateinit var itemsList: ArrayList<Items>

        fun newInstance(
            itemsList: ArrayList<Items>
        ): BookPagerFragment {
            val args = Bundle()
            args.putSerializable("newItemList", itemsList)
            val fragment = BookPagerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        itemsList = arguments?.get("newItemList") as ArrayList<Items>
        freeItemsListSize = itemsList.filter { x -> x.saleInfo.saleability == "FREE" }.size
        paidItemsListSize = itemsList.size - freeItemsListSize

        viewPager = inflater.inflate(R.layout.fragment_book_pager, container, false) as ViewPager2?
        viewPager?.adapter = BookPagerAdapter(this, itemsList)
        activity?.let { hideKeyboard(it) }
        // Set the current position and add a listener that will update the selection coordinator when
        // paging the images.
        MainActivity.currentPositionToShowOnSmallList = getPositionOnSmallList()

        viewPager!!.setCurrentItem(MainActivity.currentPositionToShowOnSmallList, false)
        viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
//                MainActivity.currentPositionToShowOnSmallList = position
//                MainActivity.currentPositionOnMainList = getPositionOnMainList()

                super.onPageSelected(position)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                MainActivity.currentPositionToShowOnSmallList = position
                MainActivity.currentPositionOnMainList = getPositionOnMainList()

                prepareExitSharedElementTransition(position)

                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
        })
        prepareEnterSharedElementTransition()

        // Avoid a postponeEnterTransition on orientation change, and postpone only of first creation.
        if (savedInstanceState == null) {
            postponeEnterTransition()
        }
        return viewPager
    }

    private fun getPositionOnSmallList(): Int {
        val position: Int = MainActivity.currentPositionOnMainList
        if (position <= freeItemsListSize && freeItemsListSize != 0) {
            return position - 1
        } else if (position >= freeItemsListSize + 1 && freeItemsListSize != 0) {
            return position - 2
        } else {
            return position - 1
        }
    }

    private fun getPositionOnMainList(): Int {
        val positionOnSmallList: Int = MainActivity.currentPositionToShowOnSmallList

        if (positionOnSmallList < freeItemsListSize && freeItemsListSize != 0) {
            return positionOnSmallList + 1
        } else if (positionOnSmallList >= freeItemsListSize && freeItemsListSize != 0) {
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

                    val image: ImageView = view2.findViewById(R.id.image_book)
                    sharedElements[names[0]] = image
                }
            })
    }

    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }



    private fun prepareExitSharedElementTransition(position : Int) {
        val transition = TransitionInflater
            .from(context)
            .inflateTransition(R.transition.image_shared_element_transition)

        sharedElementReturnTransition = transition

        // A similar mapping is set at the GridFragment with a setExitSharedElementCallback.
        setExitSharedElementCallback(
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

                    val image: ImageView = view2.findViewById(R.id.image_book)
                    image.transitionName = itemsList[position].etag
                    sharedElements[image.transitionName] = image
                }
            })
    }





}