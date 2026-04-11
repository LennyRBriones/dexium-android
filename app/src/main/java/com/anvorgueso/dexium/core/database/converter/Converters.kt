package com.anvorgueso.dexium.core.database.converter

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val intListType = Types.newParameterizedType(List::class.java, Integer::class.java)
    private val intListAdapter = moshi.adapter<List<Int>>(intListType)

    @TypeConverter
    fun fromIntList(value: List<Int>): String {
        return intListAdapter.toJson(value)
    }

    @TypeConverter
    fun toIntList(value: String): List<Int> {
        return intListAdapter.fromJson(value) ?: emptyList()
    }
}
