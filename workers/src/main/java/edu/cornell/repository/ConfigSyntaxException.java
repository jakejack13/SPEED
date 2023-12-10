package edu.cornell.repository;

/**
Thrown whenever syntax of the config file is incorrect.
*/
public class ConfigSyntaxException extends Exception {
    public ConfigSyntaxException(String message) {
        super(message);
    }
}
