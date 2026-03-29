package com.wall.bizhi.core.xpath

import org.jsoup.Jsoup
import org.w3c.dom.Document
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory
import org.xml.sax.InputSource

class XPathParser {
    fun parse(html: String, xpath: String): List<String> {
        val document = toXmlDocument(html)
        val expression = XPathFactory.newInstance().newXPath().compile(xpath)
        val nodes = expression.evaluate(document, XPathConstants.NODESET) as org.w3c.dom.NodeList
        return buildList {
            for (i in 0 until nodes.length) {
                add(nodes.item(i).textContent.trim())
            }
        }.filter { it.isNotBlank() }
    }

    private fun toXmlDocument(html: String): Document {
        val xhtml = Jsoup.parse(html).outputSettings(org.jsoup.nodes.Document.OutputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml)).outerHtml()
        val factory = DocumentBuilderFactory.newInstance().apply { isNamespaceAware = false }
        val builder = factory.newDocumentBuilder()
        return builder.parse(InputSource(StringReader(xhtml)))
    }
}
