package edu.cornell.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * Parses the config file of the repository.
 */
public class ConfigParser {
    private EnumMap<Category, List<String>> configMap = new EnumMap<>(Category.class);
    private Path configFilePath;
    public static class ConfigSyntaxException extends Exception {
        public ConfigSyntaxException(String message) {
            super(message);
        }
    }

    /**
     * Parses config files
     * @param configFilePath Path of the config file.
     */
    public ConfigParser(Path configFilePath) throws IOException, ConfigSyntaxException {
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
                    currentCategory = Category.valueOf(categoryName.trim().toUpperCase().replace(" ", "_"));
                    currentItems = new ArrayList<>();
                    categoryOpen = true;
                } catch (IllegalArgumentException e) {
                    throw new ConfigSyntaxException("Invalid category name: " + categoryName.trim());
                }
            } else if (line.trim().equals("::")) {
                if (!categoryOpen)
                    throw new ConfigSyntaxException("Category end marker :: found without a matching start marker.");
                configMap.put(currentCategory, currentItems);
                categoryOpen = false;
            } else if (line.startsWith("-")) {
                if (!categoryOpen)
                    throw new ConfigSyntaxException("Item outside of a category: " + line);
                currentItems.add(line.substring(1));
            } else if (!line.trim().isEmpty()) {
                if (!categoryOpen || currentItems.isEmpty())
                    throw new ConfigSyntaxException("Continuation line outside of an item: " + line);
                int lastIndex = currentItems.size() - 1;
                String lastItem = currentItems.get(lastIndex);
                currentItems.set(lastIndex, lastItem + line);
            }
        }

        if (categoryOpen)
            throw new ConfigSyntaxException("File ended without closing category with ::");
    }


    /**
     * Gets the values associated with the config category.
     * @param categoryType The type of category to get the information of.
     * @return A List of the category values as strings or null if category doesn't exist.
     */
    public List<String> getConfigMapCategory(Category categoryType) {
        return configMap.get(categoryType);
    }

    public enum Category {
        BUILD_COMMANDS,
        TEST_PATHS,
        TEST_RUNNER
    }
}

