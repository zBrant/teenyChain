This Java project utilizes Google's GSON library to transform Java objects into JSON format and vice versa. It's a useful tool for data serialization and deserialization in Java applications.

## Requirements
- Some version of Java and JDK
- GSON library.
- Bounceycastle

## Configuration
Make sure to have the GSON library added to your project. You can do this manually by downloading the JAR file or using a dependency manager like Maven or Gradle.

### Maven
```xml
<dependencies>
    <!-- GSON -->
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.6.2</version> <!-- Or the latest version -->
    </dependency>

    <!-- Bouncy Castle -->
    <dependency>
        <groupId>org.bouncycastle</groupId>
        <artifactId>bcpkix-jdk15on</artifactId>
        <version>1.68</version> <!-- Or the latest version -->
    </dependency>
</dependencies>
```