package com.example.androidgooglebooksapi.api

import com.example.androidgooglebooksapi.models.bookList.BookList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiRepository {
    @GET("books/v1/volumes?")
    fun getBookList(@Query("q") title : String): Call<BookList>

}