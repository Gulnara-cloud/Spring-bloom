package com.gulnara.internship;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class InternProjectApplicationTests {

	@MockBean
	private com.gulnara.internship.config.JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockBean
	private com.gulnara.internship.service.JwtService jwtService;

	@MockBean
	private com.gulnara.internship.service.UserService userService;

	@Test
	void contextLoads() {
	}
}

