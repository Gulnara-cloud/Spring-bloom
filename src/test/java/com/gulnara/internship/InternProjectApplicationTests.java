package com.gulnara.internship;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Disabled("Disabled temporarily because full context cannot be loaded with current config")
class InternProjectApplicationTests {

	@Test
	void contextLoads() {
	}
}
