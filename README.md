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

## Docker

Build the image from the repository root:

```powershell
docker build -t lost-n-found-backend:local .
```

Run the API on port 8080, passing the local database settings only at runtime:

```powershell
docker run --rm --name lost-n-found-api -p 8080:8080 --env-file .env lost-n-found-backend:local
```

The `.dockerignore` excludes local environment files, build output, Git and IDE
files, and `local.properties`; the image does not contain database credentials.
The container expects `SUPABASE_JDBC_URL`, `SUPABASE_DB_USERNAME`, and
`SUPABASE_DB_PASSWORD` at runtime. Create `.env` from `.env.example` and use
current Supabase credentials; do not commit `.env`.

To check the container, watch its startup output for `Started ApiApplication`,
then from a second terminal run:

```powershell
Invoke-WebRequest http://localhost:8080/actuator/health
```

Stop the container with Ctrl+C. To run the existing automated tests before
building the image:

```powershell
.\gradlew.bat test
```

### Local API and PostgreSQL with Compose

Compose starts a separate local PostgreSQL database and initializes it from
`database/migrations/001_initial_schema.sql` the first time its data volume is
created. It does not connect to or change the Supabase database. Add local-only
`POSTGRES_DB`, `POSTGRES_USER`, and `POSTGRES_PASSWORD` values to the root `.env`
file (the names and placeholders are in `.env.example`). Do not reuse the
Supabase password for the local database. Compose maps these local settings to
the API datasource variables; the Supabase entries in `.env` are not passed to
the Compose API container.

From the repository root, start both services:

```powershell
docker compose config --quiet
docker compose up --build
```

In another terminal, verify the API health endpoint:

```powershell
Invoke-WebRequest http://localhost:8080/actuator/health
```

Stop the services with Ctrl+C, or run `docker compose down` in another terminal.
The named PostgreSQL volume is kept so local data remains available on the next
start.
