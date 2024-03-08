package com.puras.itoandroidassignment.util

import android.util.Xml
import com.puras.itoandroidassignment.domain.entity.EntryResponse
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

class XMLParser {

    private val ns: String? = null

    @Throws(XmlPullParserException::class, IOException::class)
    operator fun invoke(data: String): List<EntryResponse> {
        val inputStream: InputStream = data.byteInputStream()
        inputStream.use { stream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(stream, null)
            parser.nextTag()
            return readFeed(parser)
        }
    }

    private fun readFeed(parser: XmlPullParser): List<EntryResponse> {
        val entries = mutableListOf<EntryResponse>()
        parser.require(XmlPullParser.START_TAG, ns, "feed")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            // Starts by looking for the entry tag.
            if (parser.name == "entry") {
                entries.add(readEntry(parser))
            } else {
                skip(parser)
            }
        }
        return entries
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readEntry(parser: XmlPullParser): EntryResponse {
        parser.require(XmlPullParser.START_TAG, ns, "entry")
        var id: String? = null
        var published: String? = null
        var link: String? = null
        var title: String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "id" -> id = readId(parser)
                "published" -> published = readPublished(parser)
                "link" -> link = readLink(parser)
                "title" -> title = readTitle(parser)
                else -> skip(parser)
            }
        }
        return EntryResponse(id = id, published = published, link = link, title = title)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readId(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "id")
        val id = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "id")
        return id
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readTitle(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "title")
        val title = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "title")
        return title
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readPublished(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "published")
        val published = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "published")
        return published
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readLink(parser: XmlPullParser): String {
        var link = ""
        parser.require(XmlPullParser.START_TAG, ns, "link")
        val tag = parser.name
        val relType = parser.getAttributeValue(null, "rel")
        if (tag == "link") {
            if (relType == "alternate") {
                link = parser.getAttributeValue(null, "href")
                parser.nextTag()
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "link")
        return link
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }
}