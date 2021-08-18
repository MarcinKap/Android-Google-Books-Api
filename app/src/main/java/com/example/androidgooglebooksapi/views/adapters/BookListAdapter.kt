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
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.androidgooglebooksapi.MainActivity
import com.example.androidgooglebooksapi.views.fragments.BookPagerFragment
import java.util.concurrent.atomic.AtomicBoolean

class BookListAdapter(var booksList: List<Items>, fragment: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val fragment = fragment
    private val VIEW_TYPE_SECTION: Int = 0
    private val VIEW_TYPE_ITEM = 1
    private var freeBooksList: ArrayList<Items> = ArrayList()
    private var paidBooksList: ArrayList<Items> = ArrayList()
    private var newItemsList: ArrayList<Items> = ArrayList()
    private var viewHolderListener: ViewHolderListener? = null

    init{
        booksList.forEach {
            if ("FREE".equals(it.saleInfo.saleability)) {
                freeBooksList.add(it)
            } else {
                paidBooksList.add(it)
            }
        }
        newItemsList.addAll(freeBooksList)
        newItemsList.addAll(paidBooksList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        viewHolderListener = ViewHolderListenerImpl(fragment, newItemsList, freeBooksList, paidBooksList)


        val inflater = LayoutInflater.from(parent.context)

        if (viewType == 0) { //Create section with title (Free/Paid books)
            val view: View = inflater.inflate(R.layout.adapter_section_book, parent, false)
            return BookSectionViewHolder(view)
        } else { //Create item with book
            val view: View = inflater.inflate(R.layout.adapter_single_book, parent, false)
            return SingleBookViewHolder(
                view,
                Glide.with(fragment),
                viewHolderListener!!
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //Now check wich ViewHolder should be run
        if (holder.itemViewType == VIEW_TYPE_SECTION) {
            if (position < freeBooksList.size + 1 && freeBooksList.size != 0) {
                (holder as BookSectionViewHolder).bind("Free book list")
            } else {
                (holder as BookSectionViewHolder).bind("Paid book list")
            }
        } else {
            var singleBook = getSingleBook(position)
            if (singleBook == null) {
                return;
            }
            (holder as SingleBookViewHolder).bind(
                singleBook
            )
        }
    }

    private fun getSingleBook(position: Int): Items {
        if (position < freeBooksList.size + 1 && freeBooksList.size != 0) {
            return freeBooksList[position - 1]
        } else if (position >= freeBooksList.size + 1 && freeBooksList.size != 0) {
            return paidBooksList[position - freeBooksList.size - 2]
        } else {
            return paidBooksList[position - 1]
        }
    }

    override fun getItemCount(): Int {
        if (freeBooksList.size == 0 || paidBooksList.size == 0) {
            return booksList.size + 1
        } else {
            return booksList.size + 2
        }
    }


    //Definition of view type (Section or Item)
    override fun getItemViewType(position: Int): Int {
        if (position == 0 || (freeBooksList.size != 0 && position == freeBooksList.size + 1)) {
            return 0; //Section
        } else {
            return 1; //Item
        }
//        return super.getItemViewType(position)
    }

    class BookSectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //create section book list adapter
        fun bind(title: String) {
            var sectionTitle: TextView = itemView.findViewById(R.id.sectionTitle)
            sectionTitle.setText(title)
        }
    }


    class SingleBookViewHolder(
        itemView: View,
        requestManager: RequestManager,
        viewHolderListener: ViewHolderListener
    ) : RecyclerView.ViewHolder(itemView) {

        private lateinit var bookImage: ImageView
        private lateinit var bookTitle: TextView
        private lateinit var bookContainer: CardView
        private lateinit var viewHolderListener: ViewHolderListener

        init {
            bookImage = itemView.findViewById(R.id.image_book)
            bookTitle = itemView.findViewById(R.id.book_title_text_view)
            bookContainer = itemView.findViewById(R.id.single_book)
            this.viewHolderListener = viewHolderListener
        }

        //create single book adapter
        fun bind(
            singleBook: Items
        ) {
//            bookImage.transitionName = java.lang.String.valueOf(singleBook)
            bookTitle.setText(singleBook.volumeInfo.title)
            bookTitle.transitionName = singleBook.etag+"title"
            bookImage.transitionName = singleBook.etag
            setImage(singleBook)

            bookContainer.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View?) {
                    viewHolderListener.onItemClicked(view, getAdapterPosition())
                }
            })
        }

        fun setImage(singleBook: Items) {
            //Download image
            if (singleBook.volumeInfo != null && singleBook.volumeInfo.imageLinks != null && singleBook.volumeInfo.imageLinks.thumbnail != null) {
                bookImage.setImageDrawable(null)
                bookImage.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                bookImage.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
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
                    .into(bookImage);
            } else {
                bookImage.setImageDrawable(null)
                bookImage.setBackgroundResource(R.drawable.no_photo)
                bookImage.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                bookImage.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
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
        private val newItemsList: ArrayList<Items>,
        private val freeItemList: ArrayList<Items>,
        private val paidBookList: ArrayList<Items>
    ) : ViewHolderListener {

        private val enterTransitionStarted: AtomicBoolean

        init {
            enterTransitionStarted = AtomicBoolean()
        }

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
//            (fragment.exitTransition as TransitionSet?)!!.excludeTarget(view, true)

            val bookImage :ImageView = view!!.findViewById(R.id.image_book)
            val bookTitle :TextView = view!!.findViewById(R.id.book_title_text_view)

            (fragment.view?.context as AppCompatActivity).supportFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true) // Optimize for shared element transition
                .addSharedElement(bookImage, bookImage.transitionName)
                .addSharedElement(bookTitle, bookTitle.transitionName)
                .replace(
                    R.id.container_fragment,
                    BookPagerFragment.newInstance(newItemsList, freeItemList, paidBookList)
                )
                .addToBackStack(null)
                .commit()
//            ,BookPagerFragment::class.java.getSimpleName()

        }


    }


}


