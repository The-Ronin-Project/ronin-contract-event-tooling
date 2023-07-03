# Event Contract Tooling

This project builds a gradle plugin (see [ronin-contract-json-plugin](ronin-contract-json-plugin)) and a docker image that _runs_ that gradle plugin if you don't
want to install gradle locally.

# Gradle Plugin

For information on the Gradle plugin, see [ronin-contract-json-plugin](ronin-contract-json-plugin).

# Tools

## Validation

Validation is done though the gradle plugin, using [com.networknt:json-schema-validator](https://github.com/networknt/json-schema-validator).

## Docs

Documentation generation uses [json-schema-for-humans](https://github.com/coveooss/json-schema-for-humans).

# Docket Usage (Deprecated)

`docker run -it -v "<schema_root_directory>:/app" ronin-contract-json-tooling:<tag> contract-tools [clean|test|doc|initialize]`

`clean` (deprecated: use gradle plugin): Remove all generated files.

`test` (deprecated: use gradle plugin): Test all versioned schemas against the curated examples.

`docs` (deprecated: use gradle plugin): Generate HTML documentation for each versioned schema.

`initialize`: Updates a Makefile/docker image based repository to use the updated plugin.  Note that it will overwrite any gradle files already in the repository (or maybe it will fail).
