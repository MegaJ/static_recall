package yiwejeje.staticrecallapp;

import android.content.Context;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
    List<ItemCategory> allCategories;
    public static final String DEFAULT_CATEGORY = "Uncategorized";
    private Gson gson;

    private CategoryManager() {

        allCategories = new ArrayList<ItemCategory>();
        gson = new GsonBuilder().create();
        initializePresetData();
//        System.out.print("---------> GSON PRINTING");
//        gson.toJson("Hello gson", System.out);
//        gson.toJson(123, System.out);
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

    public void save(Context context) {

        File file = new File(context.getFilesDir(), SAVED_DATA);

    }

    public void dataToJson(Context context) throws IOException {

        Writer writer = new FileWriter(context.getFilesDir() + "/" + SAVED_DATA);

        Gson gson = new GsonBuilder().create();

        for (ItemCategory aCategory : allCategories) {
            gson.toJson(aCategory, writer);
        }
        writer.close();

        System.out.println("-----> Attempted to save file");


        File file = new File(context.getFilesDir(), SAVED_DATA);
        InputStream inFileStream = new FileInputStream(file);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inFileStream));
        String capturedString = bufferedReader.readLine();

        while (capturedString != null) {
            System.out.println("-------> Line of JSON: " + capturedString);
            capturedString = bufferedReader.readLine();
        }

        bufferedReader.close();
        inFileStream.close();
    }

    // no syntaex errors.
    public void retrieve(Context context) throws IOException {
        System.out.println("-----> Attempting to retrieve file!");
        File file = new File(context.getFilesDir(), SAVED_DATA);
        System.out.println("-----> file created");
        InputStream source = new FileInputStream(file);
        System.out.println("-----> input stream made from file");
        Reader reader = new InputStreamReader(source);

        // do stuff here

        source.close();
        reader.close();

        // the deserializer needs info on type
        Type listOfItemCategories = new TypeToken<List<ItemCategory>>(){}.getType();
        // s will be text stored in a text file with .json extension
        String s = gson.toJson(allCategories, listOfItemCategories);
        System.out.println("------> allCategories to JSON: " + s);
        List<ItemCategory> list2 = gson.fromJson(s, listOfItemCategories);
//        ItemCategory aCategory = gson.fromJson(reader, ItemCategory.class);

        System.out.println(list2);


    }

    private void initializePresetData() {
        ItemCategory uncategorized = new ItemCategory("Uncategorized");
        ItemCategory medical = new ItemCategory("Medical");
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

        medical.addItem("Shot Record");
        medical.addItem("Antibiotics");
        medical.addItem("Birth Control");
        medical.addItem("Pamphlet about the Flu Shot");
        medical.addItem("Doctor's Business Card");

        Item momsSpaghettiRecipe = new Item("Mom's Spaghetti Recipe");
        momsSpaghettiRecipe.addCategory(docs);

        Item raisinBread = new Item("Raisin Bread");
        travel.addItem(raisinBread);
        medical.addItem(raisinBread);
        medical.removeItem(raisinBread);
        raisinBread.removeCategory(travel);

        raisinBread.addCategory(medical);
        raisinBread.addCategory(medical);
        raisinBread.addCategory(medical);

        this.addCategory(uncategorized);
        this.addCategory(docs);
        this.addCategory(medical);
        this.addCategory(travel);

        getCategoryByName("Travel").setCategoryName("Travole");

        for (Item item : this.getAllItems()) {
            System.out.println("----------> TEST: " + item + ": categories: " + item.getCategories());
        }

        for (ItemCategory aCategory : allCategories) {
            System.out.println("----------> TEST: " + aCategory + ": items: " + aCategory.getItems());
        }
    }
}