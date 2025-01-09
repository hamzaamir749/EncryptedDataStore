package com.coderpakistan.encrypteddatastore.utils

import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.util.Base64


@Serializable
data class UserPreferences(
    val token: String? = null
)

object UserPreferenceSerializer : Serializer<UserPreferences> {
    override val defaultValue: UserPreferences
        get() = UserPreferences()

    override suspend fun readFrom(input: InputStream): UserPreferences {
        val encryptedBytes = withContext(Dispatchers.IO) {
            input.use {
                it.readBytes()
            }

        }
        val decryptedBytesDecoded = Base64.getDecoder().decode(encryptedBytes)
        val decodedJsonBytes = CryptoHelper.decrypt(decryptedBytesDecoded)
        val decodedJsonString = decodedJsonBytes.decodeToString()
        return Json.decodeFromString(decodedJsonString)
    }

    override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
        val json = Json.encodeToString(t)
        val bytes = json.toByteArray()
        val encryptedBytes = CryptoHelper.encrypt(bytes)
        val encryptedBytesBase64 = Base64.getEncoder().encodeToString(encryptedBytes)
        withContext(Dispatchers.IO) {
            output.use {
                it.write(encryptedBytesBase64.toByteArray())
            }
        }
    }

}