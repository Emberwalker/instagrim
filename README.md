# Instagrim
AC32007 Secure Internet Programming (University of Dundee)

Built on the sources of the original
[acobley/Instagrim](https://github.com/acobley/Instagrim) repository

## Running
Just want to run the thing locally? These commands spin up an embedded instance
of Tomcat 8 on port 8880, with context path `/Instagrim` which can be killed by
pressing any key after the prompt. This also theoretically performs hot-reload
when any part of the project is changed. Note that you still need a working
instance of Cassandra listening on localhost for this to work!

- Windows: `.\gradlew.bat appRun`
- Mac/Linux: `./gradlew appRun`

## Building
If you have `gradle` >= 3.0, you can use your locally installed installation,
else use the wrapper as below. Build artifacts are placed in `build/libs`

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
