package edu.cornell.repository;

import lombok.NonNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * Parses the config file of the repository.
 * Expected format of config file is as follows:
 * CATEGORY::
 * - inputOne of the category
 * - inputTwo of the category
 * this is still inputTwo because there was no dash
 * ::
 * <p>
 * CATEGORY::
 * - inputOne of the category
 * this is still inputOne because there was no dash
 * ::
 */
public final class ConfigParser {
    /**
     * Maps the category to the category's respective information from the config file.
     */
    private final @NonNull EnumMap<Category, List<String>> configMap = 
        new EnumMap<>(Category.class);

    /**
     * Path of the config file.
     */
    private final @NonNull Path configFilePath;

    /**
     * Parses config files.
     * @param configFilePath Path of the config file.
     */
    public ConfigParser(@NonNull Path configFilePath) throws IOException, ConfigSyntaxException {
        this.configFilePath = configFilePath;
        parseConfig();
    }

    /**
     * Parses config file and puts all categories in map.
     * @throws IOException if can't read config file.
     * @throws ConfigSyntaxException if config file syntax is incorrect.
     */
    private void parseConfig() throws IOException, ConfigSyntaxException {
        List<String> lines = Files.readAllLines(this.configFilePath);
        Category currentCategory = null;
        ArrayList<String> currentItems = new ArrayList<>();
        boolean categoryOpen = false;

        for (String line : lines) {
            if (line.endsWith("::") && !line.startsWith("::")) {
                String categoryName = line.substring(0, line.length() - 2);
                try {
                    currentCategory = Category.valueOf(categoryName.trim().toUpperCase()
                        .replace(" ", "_"));
                    currentItems = new ArrayList<>();
                    categoryOpen = true;
                } catch (IllegalArgumentException e) {
                    throw new ConfigSyntaxException("Invalid category name: " + 
                        categoryName.trim());
                }
            } else if (line.trim().equals("::")) {
                if (!categoryOpen) {
                    throw new ConfigSyntaxException("Category end marker :: found without a " +
                        "matching start marker.");
                }
                configMap.put(currentCategory, currentItems);
                categoryOpen = false;
            } else if (line.startsWith("-")) {
                if (!categoryOpen) {
                    throw new ConfigSyntaxException("Item outside of a category: " + line);
                }
                currentItems.add(line.substring(1).trim());
            } else if (!line.trim().isEmpty()) {
                if (!categoryOpen || currentItems.isEmpty()) {
                    throw new ConfigSyntaxException("Continuation line outside of an item: " + 
                        line);
                }
                int lastIndex = currentItems.size() - 1;
                String lastItem = currentItems.get(lastIndex);
                currentItems.set(lastIndex, lastItem + line);
            }
        }

        if (categoryOpen) {
            throw new ConfigSyntaxException("File ended without closing category with ::");
        }
    }


    /**
     * Gets the values associated with the config category.
     * @param categoryType The type of category to get the information of.
     * @return A List of the category values as strings or null if category doesn't exist.
     */
    public List<String> getConfigMapCategory(Category categoryType) {
        return configMap.get(categoryType);
    }

    /**
     * The currently supported categories of information in the config file.
     * Needs updated for each new category supported.
     */
    public enum Category {
        BUILD_COMMANDS,
        TEST_PATHS,
        TEST_RUNNER
    }
}
