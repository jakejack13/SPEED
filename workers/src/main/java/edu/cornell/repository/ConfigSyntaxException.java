package edu.cornell.repository;

import java.io.Serial;

/**
  * Thrown whenever syntax of the config file is incorrect.
  */
public class ConfigSyntaxException extends Exception {
    @Serial
    private static final long serialVersionUID = 7654321L;

    /**
     * Creates a new ConfigSyntaxException.
     * @param message the message of the exception
     */
    public ConfigSyntaxException(String message) {
        super(message);
    }
}
