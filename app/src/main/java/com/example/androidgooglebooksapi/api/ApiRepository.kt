package com.example.androidgooglebooksapi.api

import com.example.androidgooglebooksapi.models.bookList.BookList
import retrofit2.Call
import retrofit2.http.GET

interface ApiRepository {

    @GET("books/v1/volumes?q=harrypotter")
    fun getNews(): Call<BookList>

}