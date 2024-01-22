package edu.cornell.repository;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * An object representing the SPEED config file in the repository
 */
@Slf4j
public class Config {

    /**
     * Parser for parsing config file.
     */
    private ConfigParser parser;

    /**
     * Creates config file.
     */
    public Config(String configFilePath) throws ConfigSyntaxException {
        try {
            parser = new ConfigParser(Path.of(configFilePath));
        } catch (IOException e) {
            LOGGER.error("Error reading config file from disk", e);
            System.exit(1);
        }
    }
    /**
     * Returns the list of commands to build the repository
     * @return the list of commands to build the repository
     */
    public @NonNull List<String> getBuildCommands() {
        return parser.getConfigMapCategory(ConfigParser.Category.BUILD_COMMANDS);
    }

    /**
     * Return list of strings representing TEST_PATHS specified in the config
     * @return list of strings of path to project files
     */
    public @NonNull List<String> getTestPaths() {
        return parser.getConfigMapCategory(ConfigParser.Category.TEST_PATHS);
    }
}
