package com.xmpp_client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class FileBase64 {
    public static String encodeFileToBase64(String filePath) throws IOException {
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
        return Base64.getEncoder().encodeToString(fileContent);
    }

    public static void decodeBase64ToFile(String encodedString, String outputPath) throws IOException {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        Files.write(Paths.get(outputPath), decodedBytes);
    }
}
