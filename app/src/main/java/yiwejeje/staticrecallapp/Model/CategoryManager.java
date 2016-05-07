package yiwejeje.staticrecallapp.Model;

import android.content.Context;

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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.google.gson.graph.GraphAdapterBuilder;

/**
 * Created by Static Recall Heroes on 3/8/16.
 * The entry point of the Model for all Activities. Holds all in-memory model information. The
 * hierarchy is CategoryManager, ItemCategory, and Item. ItemCategory and Item can update each other
 * so CategoryManager manages only ItemCategories.
 * ItemCategories manage their own items for the most part.
 * <p>
 *     Has persistence capability by writing ItemCategories into json files.
 * </p>
 */

public enum CategoryManager {
    INSTANCE;
    private Collection<ItemCategory> allCategories;
    public static final String SAVED_DATA = "saved_data.json";
    private Gson gson;

    private CategoryManager() {
        allCategories = new TreeSet<ItemCategory>(new CategoryComparator());
        configureGson();
        initializePresetData();
    }

    /**
     * Configures gson for serializing and deserializing. Leverages {@code GraphAdapterBuilder}
     * to resolve recursive dependencies between the {@code ItemCategory} and {@code Item} class
     * when serializing json. If not for GraphAdapterBuilder, infinite recursion.
     */
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

    /**
     * Returns a collection of {@code ItemCategory} type.
     * @return a {@code Collection} of ItemCategories.
     */
    public Collection<ItemCategory> getAllCategories() {
        return Collections.unmodifiableCollection(allCategories);
    }

    /**
     * Iterates through each category to construct a set of items which are unique by name.
     * @return a {@code Set} of Items
     */
    public Set<Item> getAllItems() {
        Set<Item> allItems = new TreeSet<Item>(new ItemComparator());
        for (ItemCategory category : allCategories) {
            allItems.addAll(category.getItems());
        }
        return allItems;
    }

    /**
     * This is the method most Activities will use to access previously stored categories.
     * @param categoryName
     *       Used to search through CategoryManager to see if this name exists for
     *       another {@code ItemCategory}.
     * @return {@code null} if no category can be found that matches {@code categoryName}. Returns
     *      the matching {@code ItemCategory} otherwise.
     */
    public ItemCategory getCategoryByName(String categoryName) {
        for (ItemCategory itemCategory : allCategories) {
            if (itemCategory.getName().equals(categoryName)) {
                return itemCategory;
            }
        }
        return null;
    }

    /**
     *
     * @param categoryName
     *       Used to search through CategoryManager to see if this name exists for
     *       another {@code ItemCategory}.
     * @return false if no category can be found that matches {@code categoryName}. True
     *      if there is a match.
     */
    private boolean hasCategoryWithName(String categoryName) {
        ItemCategory itemCategory = getCategoryByName(categoryName);
        if (itemCategory == null) {
            return false;
        }
        return true;
    }

    // ------ Adding ------

    /**
     * Add a category which has a unique name field to CategoryManager.
     * @param aCategory
     *      Used to search through CategoryManager to see if the name of the name field of
     *      this category is unique,
     * @return
     *      True if {@code aCategory} was successfully added. False otherwise.
     */
    public boolean addCategory(ItemCategory aCategory) {
        if (aCategory == null) {
            throw new IllegalArgumentException("Cannot add null category in CategoryManager");
        }

        // Exception instead of returning false because I want
        // Buttons and widgets to be greyed out if adding can't happen.
        // This function shouldn't be used for doing that, so throw exception instead.
        if (this.hasCategoryWithName(aCategory.getName())) {
            throw new IllegalStateException("Cannot add an existing category");
        }

        return allCategories.add(aCategory);
    }

    /**
     * Add a category by specifying a name for an {@code ItemCategory}.
     * @param categoryName
     *      Used to search through CategoryManager to see if this name exists for
     *      another {@code ItemCategory}
     * @return
     *      True if {@code aCategory} was successfully added. False otherwise.
     */
    public boolean addCategory(String categoryName) {
        ItemCategory newCategory = new ItemCategory(categoryName);
        return addCategory(newCategory);
    }

    // ------ Removing ------

    /**
     * Remove a category. Items within can belong to multiple categories so just remove
     * the item from {@code aCategory}
     *
     * @param aCategory
     *      A category to be removed
     * @return
     *      True if {@code aCategory} was successfully removed. False otherwise.
     */
    public boolean removeCategory(ItemCategory aCategory) {
        if (aCategory == null) {
            return false;
        }

        boolean removed = allCategories.remove(aCategory);
        return removed;
    }

    /**
     * Remove a category by it's name.
     * @param categoryName
     *      Used to search through CategoryManager to find the category to remove
     * @return
     *      True if {@code aCategory} was successfully removed. False otherwise.
     */
    public boolean removeCategory(String categoryName) {
        return removeCategory(getCategoryByName(categoryName));
    }

    // ------ Setup ------

    /**
     * Deletes the json file with the serialized Java objects.
     * No data recovery possible provided by this application once called.
     *
     * @param context
     *      Usually an android Activity since Activities extend Context.
     * @return true if the file was deleted. false if it was not.
     */
    public boolean deleteSavedData(Context context) {
        File file = new File(context.getFilesDir(), SAVED_DATA);
        return file.delete();
    }

    /**
     * Java objects are serialized by the gson library and stored in a json file.
     * Overwrites the json file every time this function is called.
     *
     * @param context
     *      Usually an android Activity since Activities extend Context.
     * @throws IOException from the {@code Writer}
     */
    public void save(Context context) throws IOException {
        Writer writer = new FileWriter(context.getFilesDir() + File.separator + SAVED_DATA);
        gson.toJson(allCategories, writer);
        writer.close();
    }

    /**
     * Loads a json file in local storage and serializes json into Java objects. Leverages
     * on the gson library to do so.
     *
     * @param context
     *      Usually an android Activity since Activities extend Context
     * @return true if load was successful. false otherwise.
     * @throws IOException
     *      Exception thrown by file writer.
     */
    public boolean load(Context context) throws IOException {
        final String jsonFileLocation = context.getFilesDir() + File.separator + SAVED_DATA;
        File jsonFile = new File(jsonFileLocation);

        if (jsonFile.exists()) {
            InputStream jsonStream = new FileInputStream(jsonFile);
            Reader jsonReader = new InputStreamReader(jsonStream, "UTF-8");

            Type listOfItemCategories = new TypeToken<Collection<ItemCategory>>(){}.getType();

            Collection<? extends ItemCategory> newCategories = gson.fromJson(jsonReader, listOfItemCategories);

            // merge loaded categories with preset categories
            for (ItemCategory newCategory : newCategories) {
                ItemCategory existingCategory = this.getCategoryByName(newCategory.getName());
                if (existingCategory != null) {
                    for (Item item : newCategory.getItems()) {
                        existingCategory.addItem(item);
                    }
                }
            }
            this.allCategories.addAll(newCategories);

            jsonStream.close();
            return true;
        }
        return false;
    }

    /**
     * Prints out saved data via sys.out. Reads the json file in the application's local storage.
     * @param context
     *      Usually an android Activity since Activities extend Context
     */
    public void printSavedData(Context context) {
        final String jsonFileLocation = context.getFilesDir() + File.separator + SAVED_DATA;
        String text = null;
        try {
            text = getStringFromFile(jsonFileLocation);
            System.out.println(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // http://stackoverflow.com/questions/12910503/read-file-as-string

    /**
     * Used as a helper for {@code printSavedData}
     * @param is
     * @return
     * @throws Exception
     */
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

    /**
     * Used as a helper for {@code printSavedData}
     * @param filePath
     * @return
     * @throws Exception
     */
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
    }
}