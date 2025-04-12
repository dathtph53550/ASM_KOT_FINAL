package com.example.asm_1.models

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class CartItemTypeAdapter : TypeAdapter<CartItem>() {
    override fun write(out: JsonWriter, value: CartItem?) {
        if (value == null) {
            out.nullValue()
            return
        }
        out.beginObject()
        out.name("id").value(value.id)
        out.name("name").value(value.name)
        out.name("image").value(value.image) // Just write the URL directly
        out.name("price").value(value.price)
        out.name("quantity").value(value.quantity)
        out.name("isChecked").value(value.isChecked)
        out.endObject()
    }

    override fun read(reader: JsonReader): CartItem? {
        reader.beginObject()
        var id = ""
        var name = ""
        var image = "" // Changed to String for URL
        var price = ""
        var quantity = 0
        var isChecked = false

        while (reader.hasNext()) {
            when (reader.nextName()) {
                "id" -> id = reader.nextString()
                "name" -> name = reader.nextString()
                "image" -> image = reader.nextString() // Just store the URL string
                "price" -> price = reader.nextString()
                "quantity" -> quantity = reader.nextInt()
                "isChecked" -> isChecked = reader.nextBoolean()
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        return CartItem(id, name, image, price, quantity, isChecked)
    }

    // These methods are no longer needed since we're using URLs directly
} 