package edu.cornell;

import lombok.extern.slf4j.Slf4j;

/**
 * The main leader application
 */
@Slf4j
public class Main {

    /**
     * Debug mode flag, used for turning on debug mode
     */
    public static final boolean DEBUG_MODE = System.getProperty("debugMode") != null &&
            "true".equalsIgnoreCase(System.getProperty("debugMode"));

    public static void main(String[] args) {

    }
}
