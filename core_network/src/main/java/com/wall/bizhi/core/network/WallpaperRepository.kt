package com.wall.bizhi.core.network

import com.wall.bizhi.core.xpath.XPathParser
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class WallpaperRepository(
    private val api: HtmlApi = Retrofit.Builder()
        .baseUrl("https://example.com/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
        .create(HtmlApi::class.java),
    private val parser: XPathParser = XPathParser()
) {
    suspend fun fetchByRule(rule: XPathRule): List<WallpaperItem> {
        if (!rule.enabled) return emptyList()
        val html = api.getHtml(rule.url)
        val values = parser.parse(html, rule.xpath)
        return values.mapIndexed { idx, value ->
            WallpaperItem(title = "${rule.name} #${idx + 1}", imageUrl = value, sourceRule = rule.name)
        }
    }

    suspend fun testRule(rule: XPathRule): List<String> {
        val html = api.getHtml(rule.url)
        return parser.parse(html, rule.xpath)
    }
}
