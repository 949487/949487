package com.wall.bizhi.core.network

import retrofit2.http.GET
import retrofit2.http.Url

interface HtmlApi {
    @GET
    suspend fun getHtml(@Url url: String): String
}
