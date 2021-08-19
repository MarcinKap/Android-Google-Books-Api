package com.example.androidgooglebooksapi.views.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidgooglebooksapi.models.bookList.BookList


class DataViewModel : ViewModel() {

    private val _data = MutableLiveData<BookList>()
    val data: LiveData<BookList> = _data

    fun setData(bookList: BookList?) {
        _data.value = bookList!!
    }

}