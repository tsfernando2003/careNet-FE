package com.CareNet.CareNet;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.CareNet.CareNet")
public class CareNetApplication {

	public static void main(String[] args) {
		// Load environment variables
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		System.setProperty("MYSQL_URL", dotenv.get("MYSQL_URL", "jdbc:mysql://localhost:3306/carenetdb"));
		System.setProperty("MYSQL_USER", dotenv.get("MYSQL_USER", "root"));
		System.setProperty("MYSQL_PASS", dotenv.get("MYSQL_PASS", "root"));
		System.setProperty("ADMIN_USER", dotenv.get("ADMIN_USER", "admin"));
		System.setProperty("ADMIN_PASS", dotenv.get("ADMIN_PASS", "changeme"));
		System.setProperty("EMAIL_USERNAME", dotenv.get("EMAIL_USERNAME", ""));
		System.setProperty("EMAIL_PASSWORD", dotenv.get("EMAIL_PASSWORD", ""));
		
		SpringApplication.run(CareNetApplication.class, args);
	}
}