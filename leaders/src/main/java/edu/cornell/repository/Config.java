package edu.cornell.repository;

import lombok.NonNull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * An object representing the SPEED config file in the repository.
 */
public class Config {

    /**
     * Parser for parsing config file.
     */
    private ConfigParser parser;

    /**
     * Creates config file.
     * @param configFilePath the path of the configuration file
     */
    public Config(String configFilePath) throws ConfigSyntaxException, IOException {
        parser = new ConfigParser(Path.of(configFilePath));
    }
    /**
     * Returns the list of commands to build the repository.
     * @return the list of commands to build the repository
     */
    public @NonNull List<String> getBuildCommands() {
        return parser.getConfigMapCategory(ConfigParser.Category.BUILD_COMMANDS);
    }

    /**
     * Return list of strings representing TEST_PATHS specified in the config.
     * @return list of strings of path to project files
     */
    public @NonNull List<String> getTestPaths() {
        return parser.getConfigMapCategory(ConfigParser.Category.TEST_PATHS);
    }
}
