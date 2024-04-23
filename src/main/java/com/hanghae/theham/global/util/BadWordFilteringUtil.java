package com.hanghae.theham.global.util;

import com.vane.badwordfiltering.BadWordFiltering;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BadWordFilteringUtil {

    public static String change(String text) {
        BadWordFiltering badWordFiltering = new BadWordFiltering();
        ClassPathResource resource = new ClassPathResource("badwords.txt");

        try {
            InputStream inputStream = resource.getInputStream();
            File tempFile = File.createTempFile("temp-badwords", ".txt");
            tempFile.deleteOnExit();

            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            badWordFiltering.readFile(tempFile, ",");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return badWordFiltering.change(text);
    }
}
