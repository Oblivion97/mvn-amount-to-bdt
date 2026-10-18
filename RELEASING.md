# Releasing

Every release goes through a pull request; `main` is protected and CI must pass.

## 1. GitHub-hosted Maven repository (every release)

1. On a branch, bump `<version>` in `pom.xml` and update the changelog in `README.md`.
2. Build and test: `mvn verify`
3. Deploy the build into this repository's Maven layout:
   ```bash
   V=1.4.1
   mvn deploy:deploy-file -DpomFile=pom.xml -Dfile=target/amount-to-bdt-$V.jar \
     -Dsources=target/amount-to-bdt-$V-sources.jar "-Durl=file://$(pwd)"
   ```
4. Commit, push, open a PR, and merge it once CI passes.

Never overwrite a released version, because Maven caches releases forever.

## 2. Maven Central (optional, once per release)

Publishing to Central lets anyone use the library with just the `<dependency>`, with no `<repositories>` block.

### One-time setup

1. **Account and namespace.** Sign in at [central.sonatype.com](https://central.sonatype.com) with GitHub as
   **Oblivion97**. The `io.github.oblivion97` namespace is verified automatically for GitHub sign-ins; check it under
   *Namespaces*.
2. **Publishing token.** In Central, go to *Account → Generate User Token* and add it to `~/.m2/settings.xml`:
   ```xml
   <settings>
     <servers>
       <server>
         <id>central</id>
         <username>TOKEN_USERNAME</username>
         <password>TOKEN_PASSWORD</password>
       </server>
     </servers>
   </settings>
   ```
3. **GPG key.** Central requires signed artifacts.
   ```bash
   gpg --gen-key
   gpg --list-keys                                    # note the key ID
   gpg --keyserver keyserver.ubuntu.com --send-keys KEY_ID
   ```

### Each release

After the release PR is merged:

```bash
git switch main && git pull
mvn -P central-release deploy
```

This builds the jar, sources and javadoc, signs them, uploads them to Central and publishes the release. The version
appears on [search.maven.org](https://search.maven.org) after roughly 15–30 minutes. A published version can never be
changed or removed.
