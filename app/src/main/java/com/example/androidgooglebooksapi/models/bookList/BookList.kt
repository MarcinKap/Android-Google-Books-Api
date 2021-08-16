package com.example.androidgooglebooksapi.models.bookList

import java.io.Serializable

data class BookList(

    val kind : String,
    val totalItems : Int,
    val items : ArrayList<Items>
) : Serializable