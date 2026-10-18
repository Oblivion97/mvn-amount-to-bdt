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
   The `<settings>` and `<servers>` wrapper is required. Without it Maven ignores the token and the upload fails with
   401 Unauthorized.
3. **GPG key.** Central requires signed artifacts. On Windows, use **Git Bash**, which includes `gpg`.
   ```bash
   gpg --full-generate-key                            # RSA and RSA, 4096 bits
   gpg --list-keys --keyid-format long                # key ID = 16 characters after "rsa4096/"
   gpg --keyserver hkps://keyserver.ubuntu.com --send-keys KEY_ID
   gpg --export-secret-keys --armor KEY_ID > signing-key.asc   # back up off this machine, then delete
   ```

### Each release

After the release PR is merged, in Git Bash:

1. Cache the GPG passphrase. It stays cached for about 10 minutes:
   ```bash
   echo test | gpg --local-user KEY_ID --sign > /dev/null && echo cached
   ```
2. Build, sign and upload:
   ```bash
   git switch main && git pull
   mvn -P central-release clean deploy
   ```
   Use a Maven and JDK with current CA certificates; on this project's Windows machine that's IntelliJ's bundled
   Maven with `JAVA_HOME` set to IntelliJ's `jbr`. The upload ends with "Deployment … has been validated" and
   **BUILD SUCCESS**.
3. **Publish.** On [central.sonatype.com](https://central.sonatype.com), go to *Publish → Deployments*, check that
   the deployment is **VALIDATED**, and click **Publish**. Uploads aren't published automatically, so you can check them
   first.

The version appears on [central.sonatype.com](https://central.sonatype.com/artifact/io.github.oblivion97/amount-to-bdt)
after roughly 15–30 minutes. A published version can never be changed or removed.
