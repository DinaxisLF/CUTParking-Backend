package com.app.backend.utils;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class MultipartUtil {
    public static HttpRequest.BodyPublisher buildMultipartBody(Path filePath, String boundary, String fileFieldName) throws IOException {
        var byteArrays = new ArrayList<byte[]>();

        String LINE_FEED = "\r\n";
        String filename = filePath.getFileName().toString();
        String mimeType = Files.probeContentType(filePath);
        if (mimeType == null) mimeType = "application/octet-stream";

        // --boundary
        // Content-Disposition: form-data; name="file"; filename="filename.png"
        // Content-Type: image/png
        String partHeader = "--" + boundary + LINE_FEED +
                "Content-Disposition: form-data; name=\"" + fileFieldName + "\"; filename=\"" + filename + "\"" + LINE_FEED +
                "Content-Type: " + mimeType + LINE_FEED + LINE_FEED;

        byteArrays.add(partHeader.getBytes(StandardCharsets.UTF_8));
        byteArrays.add(Files.readAllBytes(filePath));
        byteArrays.add(LINE_FEED.getBytes(StandardCharsets.UTF_8));

        // End boundary
        byteArrays.add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));

        return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
    }
}
