package com.wall.core.xpath

import org.jsoup.Jsoup

class XPathParser {
    fun parse(html: String, xpath: String): List<String> {
        val selector = xpathToCss(xpath)
        if (selector.isBlank()) return emptyList()
        val doc = Jsoup.parse(html)
        return doc.select(selector).mapNotNull { el ->
            el.attr("src").ifBlank { el.attr("href") }.ifBlank { el.text() }.takeIf { it.isNotBlank() }
        }
    }

    private fun xpathToCss(xpath: String): String {
        return xpath
            .replace("//", " ")
            .replace("/", " > ")
            .replace("[@class='", ".")
            .replace("']", "")
            .replace("[@id='", "#")
    }
}
