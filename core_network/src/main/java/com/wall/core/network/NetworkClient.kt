package com.wall.core.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object NetworkClient {
    val htmlApi: HtmlApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://example.com/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(HtmlApi::class.java)
    }
}
