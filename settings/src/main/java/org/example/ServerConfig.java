package org.example;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerConfig {
    public static int PORT;
    public static String HOST;

    static {
        Properties properties = new Properties();

        try {
            InputStream propertiesFile = Main.class.getResourceAsStream("/resources/server.properties");
            properties.load(propertiesFile);

            PORT             = Integer.parseInt(properties.getProperty("PORT"));
            HOST             = properties.getProperty("HOST");

        } catch (FileNotFoundException ex) {
            System.err.println("Properties config file not found");
        } catch (IOException ex) {
            System.err.println("Error while reading file");
        }
    }
}
