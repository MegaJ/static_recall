package yiwejeje.staticrecallapp;

import java.lang.reflect.Type;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ItemCategorySerializer implements JsonSerializer<ItemCategory> {
    @Override
    public JsonElement serialize(final ItemCategory category, final Type typeOfSrc, final JsonSerializationContext context) {
        System.out.println("------> ItemCategory Serialization called!");
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", category.getName());

        final JsonElement jsonItems = context.serialize(category.getItems());
        jsonObject.add("categories", jsonItems);

//        final JsonArray items = new JsonArray();
//        for (final Item item: category.getItems()) {
//            final JsonObject jsonItem = new JsonPrimitive(item);
//            items.add(jsonItem);
//        }
//        jsonObject.add("authors", items);

        return jsonObject;
    }
}