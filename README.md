# Instagrim
AC32007 Secure Internet Programming (University of Dundee)

## Building
If you have `gradle` >= 3.0, you can use your locally installed installation,
else use the wrapper as below.

- Windows: `.\gradlew.bat build`
- Mac/Linux: `./gradlew build`

## IDEs
This project is intended to be used with IntelliJ IDEA Ultimate, which can
import Gradle projects natively. Other IDEs not listed below may have Gradle
plugins available to produce valid IDE configurations - consult the Gradle
documentation and plugin registry.

The following tasks must be executed by either the Gradle wrapper (`gradlew`)
or by a local Gradle 3.0+ installation. The examples are Linux/Mac, but for
Windows `./gradlew` can be replaced with `.\gradlew.bat`

- Eclipse: `./gradlew cleanEclipse eclipse`
- IDEA (pre-Gradle): `./gradlew cleanIdea idea`
