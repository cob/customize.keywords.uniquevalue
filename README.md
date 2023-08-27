# customize.keyword.uniqueValue

## Install

```bash
cob-cli customize uniquevalue

# restart recordm
```

## How to use:

```
Fields:
    field:
        name: field1
        description: $uniqueValue
```

For more information you can consult [this link](https://learning.cultofbits.com/docs/cob-platform/admins/managing-information/available-customizations/unique-value/)

## Build

```bash
cd others/recordm-validators
mvn clean package
cp target/cob-customize-uniquevalue.jar ../../recordm/bundles/
```

## Release

1. Update `costumize.js` and increment version
2. Update `pom.xml` version
3. Build
