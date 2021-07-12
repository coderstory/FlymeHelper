/*
 * Copyright 2013 Philip Schiffer
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.coderstory.flyme.tools.licensesdialog

import android.util.Xml
import com.coderstory.flyme.tools.licensesdialog.licenses.License
import com.coderstory.flyme.tools.licensesdialog.model.Notice
import com.coderstory.flyme.tools.licensesdialog.model.Notices
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

object NoticesXmlParser {
    @Throws(Exception::class)
    fun parse(inputStream: InputStream): Notices {
        return try {
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            NoticesXmlParser.parse(parser)
        } finally {
            inputStream.close()
        }
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun parse(parser: XmlPullParser): Notices {
        val notices = Notices()
        parser.require(XmlPullParser.START_TAG, null, "notices")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val name = parser.name
            // Starts by looking for the entry tag
            if ("notice" == name) {
                notices.addNotice(NoticesXmlParser.readNotice(parser))
            } else {
                NoticesXmlParser.skip(parser)
            }
        }
        return notices
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readNotice(parser: XmlPullParser): Notice {
        parser.require(XmlPullParser.START_TAG, null, "notice")
        var name: String? = null
        var url: String? = null
        var copyright: String? = null
        var license: License? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val element = parser.name
            if ("name" == element) {
                name = NoticesXmlParser.readName(parser)
            } else if ("url" == element) {
                url = NoticesXmlParser.readUrl(parser)
            } else if ("copyright" == element) {
                copyright = NoticesXmlParser.readCopyright(parser)
            } else if ("license" == element) {
                license = NoticesXmlParser.readLicense(parser)
            } else {
                NoticesXmlParser.skip(parser)
            }
        }
        return Notice(name, url, copyright, license)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readName(parser: XmlPullParser): String {
        return NoticesXmlParser.readTag(parser, "name")
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readUrl(parser: XmlPullParser): String {
        return NoticesXmlParser.readTag(parser, "url")
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readCopyright(parser: XmlPullParser): String {
        return NoticesXmlParser.readTag(parser, "copyright")
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readLicense(parser: XmlPullParser): License {
        val license = NoticesXmlParser.readTag(parser, "license")
        return LicenseResolver.read(license)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readTag(parser: XmlPullParser, tag: String): String {
        parser.require(XmlPullParser.START_TAG, null, tag)
        val title = NoticesXmlParser.readText(parser)
        parser.require(XmlPullParser.END_TAG, null, tag)
        return title
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

    private fun skip(parser: XmlPullParser) {}
}