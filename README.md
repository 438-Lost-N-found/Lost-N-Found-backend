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

For PowerShell, set the variables in the same terminal before starting the API:

```powershell
$env:SUPABASE_JDBC_URL = "jdbc:postgresql://<host>:5432/postgres?sslmode=require"
$env:SUPABASE_DB_USERNAME = "<database-username>"
$env:SUPABASE_DB_PASSWORD = "<database-password>"
.\gradlew.bat bootRun
```

Use the database connection details from Supabase; do not use a Supabase API key
as the database password. Keep credentials in environment variables or a local
secret manager, and never commit them. `.env` files are ignored by Git, but
Spring Boot does not load them automatically.

Hibernate schema generation and SQL initialization are disabled so application
startup does not create, update, or otherwise alter the existing tables. The
normal context test uses an in-memory H2 database. To verify the actual Supabase
connection and confirm the existing `public.users`, `public.listings`, and
`public.claims` tables are visible, set all three variables above and run:

```powershell
.\gradlew.bat test --tests com.lostnfound.api.SupabaseDatabaseConnectionTests
```

That smoke test is skipped when `SUPABASE_JDBC_URL` is unset.
