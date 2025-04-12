package com.example.asm_1.models

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class ProductTypeAdapter : TypeAdapter<Product>() {
    override fun write(out: JsonWriter, value: Product?) {
        if (value == null) {
            out.nullValue()
            return
        }
        out.beginObject()
        out.name("id").value(value.id)
        out.name("name").value(value.name)
        out.name("image").value(value.image) // Write the image URL directly
        out.name("rating").value(value.rating)
        out.name("distance").value(value.distance)
        out.name("price").value(value.price)
        out.name("description").value(value.description)
        out.name("categoryId").value(value.categoryId)
        out.endObject()
    }

    override fun read(reader: JsonReader): Product? {
        reader.beginObject()
        var id = ""
        var name = ""
        var image = "" // Changed to String for URL
        var rating = ""
        var distance = ""
        var price = ""
        var description = ""
        var categoryId = "1"

        while (reader.hasNext()) {
            when (reader.nextName()) {
                "id" -> id = reader.nextString()
                "name" -> name = reader.nextString()
                "image" -> image = reader.nextString() // Simply store the URL string
                "rating" -> rating = reader.nextString()
                "distance" -> distance = reader.nextString()
                "price" -> price = reader.nextString()
                "description" -> description = reader.nextString()
                "categoryId" -> categoryId = reader.nextString()
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        return Product(id, name, image, rating, distance, price, description, categoryId)
    }

    // These methods are no longer needed since we're using URLs directly
} 