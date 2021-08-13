package com.example.androidgooglebooksapi.models.bookList

data class BookList(

    val kind : String,
    val totalItems : Int,
    val items : List<Items>
)