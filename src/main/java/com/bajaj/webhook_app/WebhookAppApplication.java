package com.bajaj.webhook_app;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@SpringBootApplication
public class WebhookAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebhookAppApplication.class, args);
	}

	@Bean
	CommandLineRunner run() {
		return args -> {

			RestTemplate restTemplate = new RestTemplate();

			// STEP 1: Call API
			String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

			Map<String, String> body = new HashMap<>();
			body.put("name", "Prathamesh");
			body.put("regNo", "9730299895");
			body.put("email", "garadprathamesh98@gmail.com");

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

			ResponseEntity<Map> response =
					restTemplate.postForEntity(url, request, Map.class);

			String webhookUrl = (String) response.getBody().get("webhook");
			String token = (String) response.getBody().get("accessToken");

			System.out.println("Webhook: " + webhookUrl);
			System.out.println("Token: " + token);

			// STEP 2: SQL QUERY
			String finalQuery = "SELECT p.AMOUNT AS SALARY, CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, d.DEPARTMENT_NAME FROM PAYMENTS p JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID WHERE DAY(p.PAYMENT_TIME) != 1 ORDER BY p.AMOUNT DESC LIMIT 1;";

			// STEP 3: Send answer
			HttpHeaders headers2 = new HttpHeaders();
			headers2.setContentType(MediaType.APPLICATION_JSON);
			headers2.set("Authorization", token);

			Map<String, String> answer = new HashMap<>();
			answer.put("finalQuery", finalQuery);

			HttpEntity<Map<String, String>> request2 =
					new HttpEntity<>(answer, headers2);

			ResponseEntity<String> result =
					restTemplate.postForEntity(webhookUrl, request2, String.class);

			System.out.println("Response: " + result.getBody());
		};
	}
}