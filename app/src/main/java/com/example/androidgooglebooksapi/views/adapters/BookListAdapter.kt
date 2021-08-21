package com.example.androidgooglebooksapi.views.adapters

import android.graphics.drawable.Drawable
import android.transition.TransitionSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidgooglebooksapi.R
import com.example.androidgooglebooksapi.models.bookList.Items
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.androidgooglebooksapi.MainActivity
import com.example.androidgooglebooksapi.views.fragments.BookPagerFragment
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.ArrayList

class BookListAdapter(private var itemsList: ArrayList<Items>, private val fragment: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val viewTypeSection: Int = 0
    private var freeBooksListSize: Int = 0
    private var paidBooksListSize: Int = 0
    private var viewHolderListener: ViewHolderListener? = null

    init {
        Collections.sort(itemsList)
        freeBooksListSize = itemsList.filter { it.saleInfo.saleability == "FREE"}.size
        paidBooksListSize = itemsList.size - freeBooksListSize
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        viewHolderListener =
            ViewHolderListenerImpl(fragment, itemsList)

        val inflater = LayoutInflater.from(parent.context)

        if (viewType == 0) { //Create section with title (Free/Paid books)
            val view: View = inflater.inflate(R.layout.adapter_section_book, parent, false)
            return BookSectionViewHolder(view)
        } else { //Create section with book
            val view: View = inflater.inflate(R.layout.adapter_single_book, parent, false)
            return SingleBookViewHolder(
                view,
                viewHolderListener!!
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //Now check which ViewHolder should be run
        if (holder.itemViewType == viewTypeSection) {
            if (position < freeBooksListSize + 1 && freeBooksListSize != 0) {
                fragment.context?.resources?.let {
                    (holder as BookSectionViewHolder).bind(
                        it.getString(
                            R.string.free_book_list
                        )
                    )
                }
            } else {
                fragment.context?.resources?.let {
                    (holder as BookSectionViewHolder).bind(
                        it.getString(
                            R.string.paid_book_list
                        )
                    )
                }
            }
        } else {
            val singleBook = getSingleBook(position)
            if (singleBook == null) {
                return
            }
            (holder as SingleBookViewHolder).bind(
                singleBook
            )
        }
    }

    private fun getSingleBook(position: Int): Items {
        if (position < freeBooksListSize + 1 && freeBooksListSize != 0) {
            return itemsList[position - 1]
        } else if (position >= freeBooksListSize + 1 && freeBooksListSize != 0) {
            return itemsList[position  - 2]
        } else {
            return itemsList[position - 1]
        }
    }

    override fun getItemCount(): Int {
        if (freeBooksListSize== 0 || paidBooksListSize == 0) {
            return itemsList.size + 1
        } else {
            return itemsList.size + 2
        }
    }


    //Definition of view type (Section or Item)
    override fun getItemViewType(position: Int): Int {
        if (position == 0 || (freeBooksListSize != 0 && position == freeBooksListSize + 1)) {
            return 0 //Section
        } else {
            return 1 //Item
        }
    }

    class BookSectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //create section book list adapter
        fun bind(title: String) {
            val sectionTitle: TextView = itemView.findViewById(R.id.sectionTitle)
            sectionTitle.text = title
        }
    }


    class SingleBookViewHolder(
        itemView: View,
        private var viewHolderListener: ViewHolderListener
    ) : RecyclerView.ViewHolder(itemView) {

        private var bookImage: ImageView = itemView.findViewById(R.id.image_book)
        private var bookTitle: TextView = itemView.findViewById(R.id.book_title_text_view)
        private var bookContainer: CardView = itemView.findViewById(R.id.single_book)


        //create single book adapter
        fun bind(
            singleBook: Items
        ) {
//            bookImage.transitionName = java.lang.String.valueOf(singleBook)
            bookTitle.text = singleBook.volumeInfo.title
            bookTitle.transitionName = singleBook.etag + "title"
            bookImage.transitionName = singleBook.etag
            setImage(singleBook)

            bookContainer.setOnClickListener { view ->
                viewHolderListener.onItemClicked(
                    view,
                    adapterPosition
                )
            }
        }

        private fun setImage(singleBook: Items) {
            //Download image
            if (singleBook.volumeInfo != null && singleBook.volumeInfo.imageLinks != null && singleBook.volumeInfo.imageLinks.thumbnail != null) {
                bookImage.setImageDrawable(null)
                bookImage.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                bookImage.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                Glide
                    .with(itemView)
                    .load(singleBook.volumeInfo.imageLinks.thumbnail)
                    .thumbnail(Glide.with(itemView.context).load(R.drawable.loading_apple))
                    .error(R.drawable.no_photo)
                    .fitCenter()
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            viewHolderListener.onLoadCompleted(bookImage, adapterPosition)
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            viewHolderListener.onLoadCompleted(bookImage, adapterPosition)
                            return false
                        }
                    })
                    .into(bookImage)
            } else {
                bookImage.setImageDrawable(null)
                bookImage.setBackgroundResource(R.drawable.no_photo)
                bookImage.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                bookImage.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                bookImage.updateLayoutParams<ConstraintLayout.LayoutParams> { verticalBias = 0.5f }
            }
        }

    }


    /**
     * A listener that is attached to all ViewHolders to handle image loading events and clicks.
     */
    interface ViewHolderListener {
        fun onLoadCompleted(view: ImageView?, adapterPosition: Int)
        fun onItemClicked(view: View?, adapterPosition: Int)
    }


    //
    //
    /**
     * Default [ViewHolderListener] implementation.
     */
    private class ViewHolderListenerImpl(
        private val fragment: Fragment,
        private val itemsList: ArrayList<Items>
    ) : ViewHolderListener {

        private val enterTransitionStarted: AtomicBoolean = AtomicBoolean()

        override fun onLoadCompleted(view: ImageView?, adapterPosition: Int) {

            // Call startPostponedEnterTransition only when the 'selected' image loading is completed.
            if (MainActivity.currentPositionOnMainList != adapterPosition) {
                return
            }
            if (enterTransitionStarted.getAndSet(true)) {
                return
            }
            fragment.startPostponedEnterTransition()
        }

        override fun onItemClicked(view: View?, adapterPosition: Int) {
            // Update the position.
            MainActivity.currentPositionOnMainList = adapterPosition

            // Exclude the clicked card from the exit transition (e.g. the card will disappear immediately
            // instead of fading out with the rest to prevent an overlapping animation of fade and move).
            (fragment.exitTransition as TransitionSet?)!!.excludeTarget(view, true)

            val bookImage: ImageView = view!!.findViewById(R.id.image_book)
            val bookTitle: TextView = view.findViewById(R.id.book_title_text_view)

            val fragmentManager : FragmentManager = (fragment.view?.context as AppCompatActivity).supportFragmentManager

            fragmentManager
                .beginTransaction()
                .setReorderingAllowed(true) // Optimize for shared element transition
                .addSharedElement(bookImage, bookImage.transitionName)
                .addSharedElement(bookTitle, bookTitle.transitionName)
                .replace(
                    R.id.container_fragment,
                    BookPagerFragment.newInstance(itemsList)
                )
                .addToBackStack(null)
                .commit()

        }


    }


}


