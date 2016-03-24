package yiwejeje.staticrecallapp;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ItemSerializer implements JsonSerializer<Item> {
    @Override
    public JsonElement serialize(final Item item, final Type typeOfSrc, final JsonSerializationContext context) {
        System.out.println("------> Item Serialization called!");
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", item.getName());
        jsonObject.addProperty("location", item.getLocationDescription());

        // serialize the itemCategories array
        final JsonElement jsonCategories = context.serialize(item.getCategories());
        jsonObject.add("categories", jsonCategories);

        // TODO: Implement serializing of picture
        // TODO: Implement serializing of audio recording

        return jsonObject;
    }
}