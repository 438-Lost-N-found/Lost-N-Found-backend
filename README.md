# Lost & Found Backend

Spring Boot backend starter for the Lost & Found application.

## Development

- Java 21 (the Gradle toolchain is configured for Java 21)
- Run the application with `.\gradlew.bat bootRun`
- Run tests with `.\gradlew.bat test`

## Supabase PostgreSQL

The backend uses Spring Data JPA and the PostgreSQL JDBC driver. At startup, set
these environment variables to connect to the existing Supabase database:

| Variable | Value |
| --- | --- |
| `SUPABASE_JDBC_URL` | Full PostgreSQL JDBC URL, for example `jdbc:postgresql://<host>:5432/postgres?sslmode=require` |
| `SUPABASE_DB_USERNAME` | Database username from the Supabase connection details |
| `SUPABASE_DB_PASSWORD` | Database password from the Supabase connection details |

For local development, create a private `.env` file in the repository root by
copying `.env.example`, then replace the placeholder values with the connection
details from Supabase. Spring Boot imports this file automatically when the app
starts:

```powershell
Copy-Item .env.example .env
# Edit .env and replace the placeholder values; do not commit it.
.\gradlew.bat bootRun
```

Use the database connection details from Supabase; do not use a Supabase API key
as the database password. Keep credentials in environment variables or a local
secret manager, and never commit them. `.env` is ignored by Git. Existing Windows
environment variables with the same names take precedence over values in `.env`;
remove any stale `SUPABASE_JDBC_URL`, `SUPABASE_DB_USERNAME`, and
`SUPABASE_DB_PASSWORD` variables from Windows and open a fresh terminal if the
app still appears to use old values.

The database password shared in chat should be rotated in Supabase before use.

Hibernate schema generation and SQL initialization are disabled so application
startup does not create, update, or otherwise alter the existing tables. The
normal context test uses an in-memory H2 database and does not require Supabase
credentials. To check that the backend's Supabase connection can see the
existing `public.users`, `public.listings`, and `public.claims` tables, run this
opt-in, read-only integration test from the repository root with `.env` present:

```powershell
$env:SUPABASE_INTEGRATION_TESTS = "true"
.\gradlew.bat test --tests com.lostnfound.api.SupabaseDatabaseConnectionTests
Remove-Item Env:SUPABASE_INTEGRATION_TESTS
```

The test uses the Spring Boot datasource configured from `.env` and is skipped
unless `SUPABASE_INTEGRATION_TESTS` is set to `true`.
