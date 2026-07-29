package com.saimanikantha.securefilevault;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"app.security.jwt.secret=test_secret_that_must_be_at_least_32_characters_long_for_hmac_sha_256"
})
class SecureFileVaultApplicationTests {

	@Test
	void contextLoads() {
	}

}
