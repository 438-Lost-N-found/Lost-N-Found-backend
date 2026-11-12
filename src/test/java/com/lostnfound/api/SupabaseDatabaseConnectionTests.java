package com.lostnfound.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "SUPABASE_JDBC_URL", matches = ".+")
class SupabaseDatabaseConnectionTests {

	@Test
	void connectsAndFindsExistingApplicationTables() throws SQLException {
		var tableNames = new HashSet<String>();

		try (var connection = DriverManager.getConnection(
				System.getenv("SUPABASE_JDBC_URL"),
				System.getenv("SUPABASE_DB_USERNAME"),
				System.getenv("SUPABASE_DB_PASSWORD"));
				var statement = connection.prepareStatement("""
						SELECT table_name
						FROM information_schema.tables
						WHERE table_schema = 'public'
						  AND table_name IN (?, ?, ?)
						""")) {
			statement.setString(1, "users");
			statement.setString(2, "listings");
			statement.setString(3, "claims");

			try (var results = statement.executeQuery()) {
				while (results.next()) {
					tableNames.add(results.getString("table_name"));
				}
			}
		}

		assertEquals(Set.of("users", "listings", "claims"), tableNames);
	}
}
