package com.gulnara.internship;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "spring.main.web-application-type=servlet")
@ActiveProfiles("test")
class InternProjectApplicationTests {

	@Test
	void contextLoads() {
		// Проверяем только, что контекст поднимается
	}
}
