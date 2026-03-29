package com.wall.core.network

import retrofit2.http.GET
import retrofit2.http.Url

interface HtmlApi {
    @GET
    suspend fun fetch(@Url url: String): String
}
