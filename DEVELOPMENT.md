Notes for developers and maintainers.

## Useful Commands

### Build
```
./gradlew build
```

### Build without Test
```
./gradlew jar
```

### Push to Bintray
```
./gradlew bintrayUpload
```

On Bintray, manually sync to Maven Central

## Release Process
1. Update version in gradle.properties
1. Full build
1. Commit and push
1. Validate Travis build
1. Push to Bintray
1. Sync to Maven Central
1. Tag the release
1. If needed, sync changes to appropriate branch
