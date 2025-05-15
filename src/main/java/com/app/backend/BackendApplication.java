package com.app.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BackendApplication {
	static {
		Dotenv dotenv = Dotenv.load();
		System.setProperty("DB_URL", dotenv.get("DB_URL"));
		System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		System.setProperty("GOOGLE_CLIENT_ID", dotenv.get("GOOGLE_CLIENT_ID"));
		System.setProperty("GOOGLE_CLIENT_SECRET", dotenv.get("GOOGLE_CLIENT_SECRET"));
		System.setProperty("jwt.secret", dotenv.get("JTW_SECRET"));
		System.setProperty("appwrite.project.id", dotenv.get("APP_WRITE_PROJECT_ID"));
		System.setProperty("appwrite.endpoint", dotenv.get("APP_WRITE_ENDPOINT"));
		System.setProperty("appwrite.api.key", dotenv.get("APP_WRITE_API"));
		System.setProperty("appwrite.bucket.id", dotenv.get("APP_WRITE_BUCKET_ID"));
	}
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
