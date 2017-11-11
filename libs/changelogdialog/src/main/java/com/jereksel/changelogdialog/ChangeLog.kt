package com.jereksel.changelogdialog

data class ChangeLog(
        val versions: MutableList<ChangeLogVersion> = mutableListOf()
) {
    fun version(name: String, init: ChangeLogVersion.() -> Unit) {
        val version = ChangeLogVersion(false, name)
        version.init()
        versions += version
    }
    fun betaVersion(name: String, init: ChangeLogVersion.() -> Unit) {
        val version = ChangeLogVersion(true, name)
        version.init()
        versions += version
    }
}

fun changelog(init: ChangeLog.() -> Unit): ChangeLog {
    val changelog = ChangeLog()
    changelog.init()
    return changelog
}