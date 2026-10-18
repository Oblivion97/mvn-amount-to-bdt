# mvn-amount-to-bdt

Public Maven repository for internal BRAC IT libraries, served straight from GitHub raw URLs.

## Use it

```xml
<repositories>
    <repository>
        <id>oblivion-github</id>
        <url>https://raw.githubusercontent.com/Oblivion97/mvn-amount-to-bdt/main/</url>
        <snapshots><enabled>false</enabled></snapshots>
    </repository>
</repositories>
```

| Artifact | Version |
|---|---|
| `com.bracits:amount-to-bdt` | 1.0.0 |

## Add a library

Run from any folder, pointing `-Durl` at this repo's local clone:

```bash
mvn deploy:deploy-file -DgroupId=com.bracits -DartifactId=my-lib -Dversion=1.0.0 -Dpackaging=jar \
  -Dfile=path/to/my-lib-1.0.0.jar -DgeneratePom=true \
  "-Durl=file:///C:/Users/Mahmu/Documents/Brac IT/HCMP/library"
git add . && git commit -m "Add my-lib 1.0.0" && git push
```

Then add the `<dependency>` to the project's pom.xml. Never overwrite a released version; bump the version instead,
because Maven caches downloaded releases forever.
