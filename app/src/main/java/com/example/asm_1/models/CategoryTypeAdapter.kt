package com.example.asm_1.models

import com.example.asm_1.R
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class CategoryTypeAdapter : TypeAdapter<Category>() {
    override fun write(out: JsonWriter, value: Category?) {
        if (value == null) {
            out.nullValue()
            return
        }
        out.beginObject()
        out.name("id").value(value.id)
        out.name("name").value(value.name)
        out.name("image").value(value.image)
        out.endObject()
    }

    override fun read(reader: JsonReader): Category? {
        reader.beginObject()
        var id = ""
        var name = ""
        var image = ""

        while (reader.hasNext()) {
            when (reader.nextName()) {
                "id" -> id = reader.nextString()
                "name" -> name = reader.nextString()
                "image" -> image = reader.nextString()
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        return Category(id, name, image)
    }

    // These methods are no longer needed as we're storing image URLs as strings directly
    // Left as comments for reference
    /*
    private fun getResourceId(imageStr: String): Int {
        return when (imageStr) {
            "R.drawable.burger_icon" -> R.drawable.burger_icon
            "R.drawable.taco_icon" -> R.drawable.taco_icon
            "R.drawable.drink_icon" -> R.drawable.drink_icon
            "R.drawable.pizza_icon" -> R.drawable.pizza_icon
            else -> R.drawable.burger_icon // default image
        }
    }

    private fun getResourceName(resourceId: Int): String {
        return when (resourceId) {
            R.drawable.burger_icon -> "burger_icon"
            R.drawable.taco_icon -> "taco_icon"
            R.drawable.drink_icon -> "drink_icon"
            R.drawable.pizza_icon -> "pizza_icon"
            else -> "burger_icon" // default image
        }
    }
    */
}