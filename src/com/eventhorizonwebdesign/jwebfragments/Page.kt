package com.eventhorizonwebdesign.jwebfragments

import java.util.*
import java.io.InputStreamReader
import java.io.BufferedReader
import java.net.URL
import java.time.LocalDateTime
import java.util.regex.Pattern


@Suppress("MemberVisibilityCanPrivate", "unused")
class Page {
    val elements = Vector<Element>()
    var url = ""
    var dateFetched: LocalDateTime? = null
    private var data = ""

    fun fetch(): String {
        var pageData = ""
        val oracle = URL(url)
        val pageReader = BufferedReader(
                InputStreamReader(oracle.openStream()))

        var inputLine: String? = pageReader.readLine()
        while (inputLine != null) {
            pageData += inputLine
            inputLine = pageReader.readLine()
        }
        pageReader.close()
        dateFetched = LocalDateTime.now()
        data = pageData
        return pageData
    }

    fun fetchElements(){
        this.fetch()
        this.calculateElements()
    }

    fun calculateElements(){
        val workingData = data
        workingData.replace("<!doctype html>", "", true)
        val htmlFindRegex = "<\\w+(\\s*.+\\r*\\n*)*>"
        val word = Pattern.compile(htmlFindRegex)
        val match = word.matcher(workingData)
        while (match.find()) {
            System.out.println("Found love at index " + match.start() + " - " + (match.end() - 1))
            val tagNameFindRegex = "<\\\\*\\w+"
            val tag = Pattern.compile(tagNameFindRegex)
            val tagMatch = tag.matcher(workingData.substring(match.start(), match.end()))
            val el = Element()
            el.outerHTML = workingData.substring(match.start(), match.end())
            if (tagMatch.find()){
                el.type = workingData.substring(match.start(), match.end()).substring(tagMatch.start() + 1, tagMatch.end())
            }
            val innerHTMLFindRegex = ">(\\s*.+\\r*\\n*)*<\\w*/"
            val inner = Pattern.compile(innerHTMLFindRegex)
            val innerMatch = inner.matcher(workingData.substring(match.start(), match.end()))
            if (innerMatch.find() && match.end() - match.start() > 3){
                el.innerHTML = workingData.substring(match.start(), match.end()).substring(innerMatch.start() + 1, innerMatch.end() - 2)
            }
            //TODO get attributes of child
            this.elements.addElement(el)
        }
        for (element in elements){
            element.calculateAttributes()
            element.calculateElements()
        }
    }
}