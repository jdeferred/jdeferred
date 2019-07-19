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
1. Make sure Bintray credentials are configured in `$HOME/.gradle/gradle.properties`
   1. Configure `bintrayUsername` and `bintrayApiKey`
   1. Configure `mavenUsername` and `mavenPassword` (Maven Central user token name and user token password)
1. Update version in `gradle.properties`
1. Full build `./gradlew build`
1. Commit and push
   1. Commit `git add gradle.properties && git commit -m "bump version to VERSION"`
   1. Push `git push origin`
1. Validate Travis build `travis status`
1. Push to Bintray `./gradlew bintrayUpload -i`
   1. This will push to JCenter and also sync w/ Maven Central
1. Tag the release
   1. Tag w/ current version `git tag VERSION`
   1. Push tags `git push --tags`
1. Update version in `gradle.properties` back to the next version SNAPSHOT
1. Commit and push
   1. Commit `git add gradle.properties && git commit -m "bump version to snapshot"`
   1. Push `git push origin`
1. If needed, sync changes to appropriate branch, e.g.:
   1. Check out the major version branch: `git checkout 2.x`
   1. Rebase: `git rebase master`
   1. push: `git push origin 2.x`
