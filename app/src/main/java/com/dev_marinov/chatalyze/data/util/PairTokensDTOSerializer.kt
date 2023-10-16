package com.dev_marinov.chatalyze.data.util

import android.util.Log
import androidx.datastore.core.Serializer
import com.dev_marinov.chatalyze.data.auth.dto.PairTokensDTO
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object PairTokensDTOSerializer: Serializer<PairTokensDTO> {
    override val defaultValue: PairTokensDTO = PairTokensDTO()

    override suspend fun readFrom(input: InputStream): PairTokensDTO {
        return try {
            Json.decodeFromString(
                deserializer = PairTokensDTO.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            Log.d("4444", " AppSettingsSerializer try catch e=" + e)
            defaultValue
        }
    }

    override suspend fun writeTo(t: PairTokensDTO, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = PairTokensDTO.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}