package org.example;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws IOException {
        // оно работало =) но не в jar
        //URL url = Main.class.getResource("/resources/server.properties");
        //assert url != null;
        //File file = new File(url.getFile());
        //System.out.println(file.exists());

        InputStream inputStream = Main.class.getResourceAsStream("/resources/server.properties");
        assert inputStream != null;
        byte[] bytes = inputStream.readAllBytes();
        String text = new String(bytes, StandardCharsets.UTF_8);
        System.out.print(text);

    }
}