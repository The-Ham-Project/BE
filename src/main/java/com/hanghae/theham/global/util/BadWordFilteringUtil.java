package com.hanghae.theham.global.util;

import com.vane.badwordfiltering.BadWordFiltering;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

public class BadWordFilteringUtil {

    public static String change(String text) {
        BadWordFiltering badWordFiltering = new BadWordFiltering();
        ClassPathResource resource = new ClassPathResource("badwords.txt");

        try {
            File file = resource.getFile();
            badWordFiltering.readFile(file, ",");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return badWordFiltering.change(text);
    }
}
