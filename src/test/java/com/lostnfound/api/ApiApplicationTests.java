package com.lostnfound.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import com.lostnfound.api.listing.AuthenticatedUserLookupTestConfig;
import org.springframework.context.annotation.Import;
@SpringBootTest
@ActiveProfiles("test")
@Import(AuthenticatedUserLookupTestConfig.class)
class ApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
