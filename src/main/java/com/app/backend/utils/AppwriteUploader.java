package com.app.backend.utils;

import org.json.JSONObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import javax.imageio.ImageIO;

@Component
public class AppwriteUploader {

    @Value("${appwrite.project.id}")
    private  String APPWRITE_PROJECT_ID;

    @Value("${appwrite.api.key}")
    private  String APPWRITE_API_KEY;

    @Value("${appwrite.bucket.id}")
    private  String BUCKET_ID;

    private static final String APPWRITE_ENDPOINT = "https://cloud.appwrite.io/v1/storage/files";

    private final OkHttpClient client = new OkHttpClient();

    public String uploadQRCode(BufferedImage image, String filename) throws IOException {
        // Convert image to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        // Multipart body
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("fileId", UUID.randomUUID().toString())
                .addFormDataPart("file", filename,
                        RequestBody.create(imageBytes, MediaType.parse("image/png")))
                .build();

        Request request = new Request.Builder()
                .url(APPWRITE_ENDPOINT + "?project=" + APPWRITE_PROJECT_ID)
                .addHeader("X-Appwrite-Project", APPWRITE_PROJECT_ID)
                .addHeader("X-Appwrite-Key", APPWRITE_API_KEY)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Upload failed: " + response.code() + " " + response.message());
            }

            String responseBody = response.body().string();
            String fileId = extractFileIdFromResponse(responseBody); // You'll need to extract fileId from JSON

            return String.format("https://cloud.appwrite.io/v1/storage/buckets/%s/files/%s/view?project=%s",
                    BUCKET_ID, fileId, APPWRITE_PROJECT_ID);
        }
    }

    private String uploadToAppwrite(String filePath, String fileFieldName) throws IOException, InterruptedException {
        String boundary = "----JavaFormBoundary" + System.currentTimeMillis();
        Path file = Paths.get(filePath);

        HttpRequest.BodyPublisher body = MultipartUtil.buildMultipartBody(file, boundary, "file");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(APPWRITE_ENDPOINT + "/storage/buckets/" + BUCKET_ID + "/files"))
                .header("X-Appwrite-Project", APPWRITE_PROJECT_ID)
                .header("X-Appwrite-Key", APPWRITE_API_KEY)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(body)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201) {
            throw new RuntimeException("Failed to upload file to Appwrite: " + response.body());
        }

        JSONObject json = new JSONObject(response.body());
        String fileId = json.getString("$id");

        return APPWRITE_ENDPOINT + "/storage/buckets/" + BUCKET_ID + "/files/" + fileId + "/view?project=" + APPWRITE_PROJECT_ID;
    }

    public String uploadFile(File file, String filename) throws IOException {
        byte[] fileBytes = java.nio.file.Files.readAllBytes(file.toPath());

        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("fileId", UUID.randomUUID().toString())
                .addFormDataPart("file", filename,
                        RequestBody.create(fileBytes, MediaType.parse("image/png")))
                .build();

        Request request = new Request.Builder()
                .url(APPWRITE_ENDPOINT + "?project=" + APPWRITE_PROJECT_ID)
                .addHeader("X-Appwrite-Project", APPWRITE_PROJECT_ID)
                .addHeader("X-Appwrite-Key", APPWRITE_API_KEY)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Upload failed: " + response.code() + " " + response.message());
            }

            String responseBody = response.body().string();
            String fileId = extractFileIdFromResponse(responseBody);

            return String.format("https://cloud.appwrite.io/v1/storage/buckets/%s/files/%s/view?project=%s",
                    BUCKET_ID, fileId, APPWRITE_PROJECT_ID);
        }
    }



    private String extractFileIdFromResponse(String json) {
        // You can use a JSON parser like Jackson or org.json here. Here's a quick hack:
        int start = json.indexOf("\"$id\":\"") + 7;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}
