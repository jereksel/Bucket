package com.jereksel.changelogdialog

data class ChangeLogVersion(
        val beta: Boolean,
        val version: String,
        var changes: MutableList<String> = mutableListOf()
) {
    operator fun String.unaryPlus() {
        changes.add(this)
    }
}
