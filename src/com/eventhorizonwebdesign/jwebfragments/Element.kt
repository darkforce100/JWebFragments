package com.eventhorizonwebdesign.jwebfragments

import java.util.*
import java.util.regex.Pattern

@Suppress("unused", "MemberVisibilityCanPrivate")
class Element{
    var type = ""
    private var tagHTML = ""
    var attributes = Vector<Attribute>()
    var children = Vector<Element>()
    var innerHTML = ""
    var outerHTML = ""

    fun calculateElements(){
        val workingData = innerHTML
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
            this.children.addElement(el)
        }
        for (child in children){
            child.calculateAttributes()
            child.calculateElements()
        }
    }

    fun calculateAttributes(){
        val workingData = if (innerHTML.isNotEmpty()){
            outerHTML.replace(innerHTML, "")
        } else {
            outerHTML
        }
        val attrFindRegex = "\\w*[:-]*\\w*=\"[A-z/:.\\d,\\s=\\-()']*\"|\\w*='[A-z/:.\\d,\\s=\\-()\"]*'"
        val attr = Pattern.compile(attrFindRegex)
        val attrMatch = attr.matcher(workingData)
        while(attrMatch.find()){
            //TODO save key/value
            val at = Attribute()
            at.key = workingData.substring(0, workingData.indexOf("=") - 1)
            at.value = workingData.substring(workingData.indexOf("=") + 2, workingData.length - 2)
            attributes.addElement(at)
        }
    }

    fun generateHTML(): String {
        var temp = ""
        temp += "<$type"
        for (attr in attributes){
            temp += " " + attr.key + "=\'" + attr.value + "\'"
        }
        temp += ">$innerHTML</$type>"
        return temp
    }
}