package com.lostnfound.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "SUPABASE_INTEGRATION_TESTS", matches = "true")
class SupabaseDatabaseConnectionTests {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void canSeeExistingApplicationTables() {
		var tableNames = jdbcTemplate.query("""
				SELECT table_name
				FROM information_schema.tables
				WHERE table_schema = 'public'
				  AND table_type = 'BASE TABLE'
				  AND table_name IN (?, ?, ?)
				""",
				(resultSet, rowNumber) -> resultSet.getString("table_name"),
				"users", "listings", "claims");

		assertEquals(Set.of("users", "listings", "claims"), Set.copyOf(tableNames));
	}
}
