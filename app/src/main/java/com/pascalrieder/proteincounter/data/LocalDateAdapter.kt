package com.pascalrieder.proteincounter.data

import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateAdapter : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun serialize(date: LocalDate, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(date.format(dateFormatter))
    }

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): LocalDate {
        return LocalDate.parse(json.asString, dateFormatter)
    }
}


