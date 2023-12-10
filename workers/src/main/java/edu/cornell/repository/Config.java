package edu.cornell.repository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * An object representing the SPEED config file in the repository
 */
public class Config {

    /**
    Parser for parsing config file.
    */
    private ConfigParser parser;

    /**
    Creates config file.
    */
    public Config(String configFilePath) throws ConfigSyntaxException, IOException {
        parser = new ConfigParser(Path.of(configFilePath));
    }
    /**
     * Returns the list of commands to build the repository
     * @return the list of commands to build the repository
     */
    public List<String> getBuildCommands() {
        return parser.getConfigMapCategory(ConfigParser.Category.BUILD_COMMANDS);
    }
}
