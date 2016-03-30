package yiwejeje.staticrecallapp;

import android.content.Context;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonStreamParser;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

/**
 * Created by Static Recall Heroes on 3/8/16.
 */

public enum CategoryManager {
    INSTANCE;
    private List<ItemCategory> allCategories;
    public static final String DEFAULT_CATEGORY = "Uncategorized";
    private Gson gson;

    private CategoryManager() {
        allCategories = new ArrayList<ItemCategory>();
        configureGson();
        initializePresetData();
    }

    private void configureGson () {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        new GraphAdapterBuilder()
                .addType(ItemCategory.class)
                .addType(Item.class)
                .registerOn(gsonBuilder);
        gson = gsonBuilder
                .serializeNulls()
                .setPrettyPrinting()
                .create();
    }

    public List<ItemCategory> getAllCategories() {
        return Collections.unmodifiableList(allCategories);
    }

    public void setAllCategories(List<ItemCategory> allCategories) {
        this.allCategories = allCategories;
    }

    public Set<Item> getAllItems() {
        Set<Item> allItems = new HashSet<Item>();
        for (ItemCategory category : allCategories) {
            allItems.addAll(category.getItems());
        }
        return allItems;
    }

    public ItemCategory getCategoryByName(String categoryName) {
        for (ItemCategory itemCategory : allCategories) {
            if (itemCategory.getName().equals(categoryName)) {
                return itemCategory;
            }
        }
        return null;
    }

    private boolean hasCategoryWithName(String categoryName) {
        ItemCategory itemCategory = getCategoryByName(categoryName);
        if (itemCategory == null) {
            return false;
        }
        return true;
    }

    // ------ Adding ------

    public boolean addCategory(ItemCategory aCategory) {
        if (aCategory == null) {
            throw new IllegalArgumentException("Cannot add null category in CategoryManager");
        }

        // Exception instead of returning false because I want
        // Buttons and widgets to be greyed out if adding can't happen.
        // This function shouldn't be used for doing that, so throw exception instead.
        if (this.hasCategoryWithName(aCategory.getName())) {
            throw new IllegalArgumentException("Cannot add an existing category");
        }

        boolean added = allCategories.add(aCategory);
        Collections.sort(allCategories, new CategoryComparator());
        return added;
    }

    public boolean addCategory(String categoryName) {
        ItemCategory newCategory = new ItemCategory(categoryName);
        return addCategory(newCategory);
    }

    // ------ Removal ------

    public Item removeByGroupAndChildIndex(int groupIndex, int childIndex) {
        return allCategories.get(groupIndex).getItems().remove(childIndex);
    }

    // Items can belong to multiple categories so just remove
    // the copy of the item from that category
    public boolean removeCategory(ItemCategory aCategory) {
        if (aCategory == null) {
            return false;
        }

        boolean removed = allCategories.remove(aCategory);
        Collections.sort(allCategories, new CategoryComparator());
        return removed;
    }

    public boolean removeCategory(String categoryName) {
        return removeCategory(getCategoryByName(categoryName));
    }

    // ------ Setup ------
    public static final String SAVED_DATA = "saved_data.json";

    public boolean deleteSavedData(Context context) {
        File file = new File(context.getFilesDir(), SAVED_DATA);
        return file.delete();
    }

    public void save(Context context) throws IOException {
        Writer writer = new FileWriter(context.getFilesDir() + "/" + SAVED_DATA);
        gson.toJson(allCategories, writer);
        writer.close();
    }

    public boolean load(Context context) throws IOException {
        final String jsonFileLocation = context.getFilesDir() + "/" + SAVED_DATA;
        File jsonFile = new File(jsonFileLocation);

        if (jsonFile.exists()) {
            System.out.println("------> Json file exists");
            InputStream jsonStream = new FileInputStream(jsonFile);
            Reader jsonReader = new InputStreamReader(jsonStream, "UTF-8");

            Type listOfItemCategories = new TypeToken<List<ItemCategory>>(){}.getType();
            this.allCategories = gson.fromJson(jsonReader, listOfItemCategories);

            jsonStream.close();
            return true;
        }
        return false;
    }

    public void printSavedData(Context context) {
        final String jsonFileLocation = context.getFilesDir() + "/" + SAVED_DATA;
        String text = null;
        try {
            text = getStringFromFile(jsonFileLocation);
            System.out.println(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // http://stackoverflow.com/questions/12910503/read-file-as-string
    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    // http://stackoverflow.com/questions/12910503/read-file-as-string
    public static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    private void initializePresetData() {
        ItemCategory uncategorized = new ItemCategory("Uncategorized");
        ItemCategory docs = new ItemCategory("Important Documents");
        ItemCategory travel = new ItemCategory("Travel");

        uncategorized.addItem("Birthday present for mom");
        uncategorized.addItem("Birthday present for mom");

        travel.addItem("Passport");
        travel.addItem("Suitcase");
        travel.addItem("Toothbrush");
        travel.addItem("Books");
        travel.addItem("Flight Ticket");
        travel.addItem("iPod");
        travel.addItem("Jacket");
        travel.addItem("Mom's goodluck charm");

        docs.addItem("Birth Certificate");
        docs.addItem("Social Security Card");
        docs.addItem("Academic Transcript");
        docs.addItem("W2 Forms");
        docs.addItem("Job Application");
        docs.addItem("Groupon for Pilates");

        Item momsSpaghettiRecipe = new Item("Mom's Spaghetti Recipe");
        momsSpaghettiRecipe.addCategory(docs);

        Item raisinBread = new Item("Raisin Bread");
        travel.addItem(raisinBread);

        raisinBread.removeCategory(travel);

        this.addCategory(uncategorized);
        this.addCategory(docs);
        this.addCategory(travel);

        for (Item item : this.getAllItems()) {
            System.out.println("----------> TEST: " + item + ": categories: " + item.getCategories());
        }

        for (ItemCategory aCategory : allCategories) {
            System.out.println("----------> TEST: " + aCategory + ": items: " + aCategory.getItems());
        }
    }
}