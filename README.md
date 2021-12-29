# FFXIV Tools

* [License](#license)
* [Technical Choices](#technical-choices)
* [Development Environment Setup](#development-environment-setup)
* [Running FFXIV Tools in Development](#running-ffxiv-tools-in-development)

## License

TBD, for now though all rights reserved.

## Technical Choices

### Backend

* Database - PostgreSQL
    * Opted for simplicity of use, sane defaults, and performance
    * Queries are written by hand but converted to Kotlin objects via SQLDelight (fairly limiting, might change)
* Dependency Injection - Dagger
    * Lightweight, proven, easy to use
* HTTP Client - Ktor
    * Pretty much the default choice for Kotlin
* Scheduler - Quartz
    * Integrates well with Jooby, great annotations
* Session Management - Redis
    * Best option for fast lookups as opposed to using the DB directly
* Web Framework - Jooby
    * Minimal, performant, interfaces very well with Kotlin coroutines

### Frontend

* CSS Framework - Bulma
    * Lighter than Bootstrap with most of the functionality still needed
* JS Framework - React (hooks)
    * Everyone knows it, completely painless setup using `react-scripts`

## Development Environment Setup

TODO: Docker image version that can do most of the below automagically?

### Linux/WSL

* Create a new Discord application [here](https://discord.com/developers/applications)
    * In the OAuth2 configuration, add a redirect to `http://localhost:3000/auth/`
    * Make note of your client id and client secret
* Install `postgresql` and `redis-server`
    * Set up a database and user account in postgres, making note of the details

## Running FFXIV Tools in Development

### Environment Setup

TODO: Environment file?

The current setup only really allows for providing environment variables via run configurations with IntelliJ IDEA.
Below is a table of what must be set in order to run all the possible processes.

| Environment Variable | Value                                    |
|----------------------|------------------------------------------|
| DATABASE_DIALECT     | postgresql                               |
| DATABASE_HOST        | \<host\>                                 |
| DATABASE_PORT        | \<port\>                                 |
| DATABASE_NAME        | \<database name\>                        |
| DATABASE_USERNAME    | \<database username\>                    |
| DATABASE_PASSWORD    | \<database password\>                    |
| OAUTH_CLIENT_ID      | \<discord oauth client id\>              |
| OAUTH_CLIENT_SECRET  | \<discord oauth client secret\>          |
| OAUTH_REDIRECT_URL   | http://localhost:3000/auth/              |
| OAUTH_TOKEN_URL      | https://discord.com/api/oauth2/token     |
| OAUTH_AUTHORIZE_URL  | https://discord.com/api/oauth2/authorize |
| REDIS_URI            | redis://\<host:port\>                    |

### Migrate the Database

* Set up an IntelliJ run configuration of type gradle
* Run: `flywayMigrate -i`
* Environment variables: Use the `DATABASE_*` variables from above
* Finally execute, run this especially after pulling to make sure you are in sync

### Run Configuration Setup

#### Frontend Setup

* Set up IntelliJ run configurations of type NPM
    * `start` - Starts a development server running on port 3000, doubles as a proxy to the backend
    * `test` - Runs unit tests, which there are none of :^)
    * `run build` - Bundles for production use

#### Backend

* Set up IntelliJ run configurations of type Kotlin with all the environment variables from above
    * Main class: `com.dkosub.ffxiv.tools.MainKt`, all environment variables above
        * Runs the main FFXIV Tools service on port 8080, is proxied to by the front-end when developing
    * Main class: `com.dkosub.ffxiv.tools.tool.ItemImporterKt`
        * Imports items and pulls marketable item data from Universalis
    * Main class: `com.dkosub.ffxiv.tools.tool.RecipeImporterKt`
        * Imports crafting recipes
        * Note: must be run after the item importer
    * Main class: `com.dkosub.ffxiv.tools.tool.ShopImporterKt`
        * Imports currency values from various shops across FFXIV
        * Note: must be run after the item importer
