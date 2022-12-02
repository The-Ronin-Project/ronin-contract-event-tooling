# Event Contract Tooling docker image

This docker image contains the tooling used to validate JSON schema contracts as well as generate documentation from the schema. 

# Tools

## Validation
Validation tooling is based on [ajv-cli](https://github.com/ajv-validator/ajv-cli) and [ajv-formats](https://github.com/ajv-validator/ajv-formats). 

## Docs
Documentation generation uses [json-schema-for-humans](https://github.com/coveooss/json-schema-for-humans). 

# Usage
`docker run -it -v "<schema_root_directory>:/app" ronin-contract-event-tooling:<tag> contract-tools [clean|test|doc]`

`clean`: Remove all generated files.

`test`: Test all versioned schemas against the curated examples. 

`docs`: Generate HTML documentation for each versioned schema.
