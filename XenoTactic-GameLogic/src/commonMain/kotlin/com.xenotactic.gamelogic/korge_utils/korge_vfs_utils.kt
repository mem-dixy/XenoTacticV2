package com.xenotactic.gamelogic.korge_utils

import com.soywiz.korio.async.runBlockingNoSuspensions
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.std.localCurrentDirVfs
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import com.xenotactic.gamelogic.model.GameMap
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

val TEST_TEMP_DATA_VFS = runBlockingNoSuspensions {
    localCurrentDirVfs["src/commonTest/testdata/TEMP"].apply {
        mkdir()
    }
}

val TEST_DATA_VFS = runBlockingNoSuspensions {
    localCurrentDirVfs["src/commonTest/testdata"].apply {
        mkdir()
    }
}

fun VfsFile.existsBlocking(): Boolean {
    return runBlockingNoSuspensions { this.exists() }
}

inline fun <reified T> VfsFile.decodeJson(): T? {
    if (existsBlocking()) {
        return Json.decodeFromString<T>(this.readStringBlocking())
    }
    return null
}

fun VfsFile.listSimpleBlocking(): List<VfsFile> {
    return runBlockingNoSuspensions { listSimple() }
}

fun VfsFile.readStringOrNull(): String? {
    if (existsBlocking()) {
        return readStringBlocking()
    }
    return null
}

fun VfsFile.readStringBlocking(): String {
    return runBlockingNoSuspensions { readString() }
}

fun VfsFile.toGameMap(): GameMap? {
    return this.decodeJson<GameMap>()
}



