package com.example.libro.network

import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApiService {
    @GET("volumes")
    suspend fun getBookByISBN(@Query("q") isbn: String): BookApiResponse
}