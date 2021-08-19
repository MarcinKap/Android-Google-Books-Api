package com.example.androidgooglebooksapi.models.bookList

import android.graphics.Bitmap


data class ImageLinks (

	val smallThumbnail : String,
	val thumbnail : String,
	var image : Bitmap
)