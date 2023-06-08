package com.medithings.blueprint.model

enum class MacroEol(val index: Int) {
    LF(0),
    CR(1),
    CR_LF(2);
}

fun String.parseWithNewLineChar(newLineChar: MacroEol): String {
    return when (newLineChar) {
        MacroEol.LF -> this
        MacroEol.CR_LF -> this.replace("\n", "\r\n")
        MacroEol.CR -> this.replace("\n", "\r")
    }
}