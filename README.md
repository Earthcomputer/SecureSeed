# SecureSeed
State-of-the-art cryptography to protect your world seed against seed cracking tools.

This mod is written for the Fabric Mod Loader. If you have advanced knowledge on coding Spigot plugins, help with porting this to Spigot would be greatly appreciated.

## How does it work?
1. Increased the number of bits in the world seed from 64 to 1024. Although cracking a 64-bit seed with the other anti-seed-cracking measures is likely infeasible at the moment without access to a huge datacenter, we cannot rule out this possibility from the future, 1024 bits is likely to be safe for a *very* long time.
1. Changed the random number generators from LCGs (and the homebrew RNG in the biome generator) to a fast cryptographically secure RNG based on the [BLAKE2](https://en.wikipedia.org/wiki/BLAKE_(hash_function)) secure hashing algorithm. This has a minor negative impact on performance of world gen, but ensures that players cannot derive any information about the world other than the blocks they can already see.
1. Changed local seeding formulas throughout the world generator to a more secure "concatenation" approach enabled by the cryptographically secure RNG. The secureness of the RNG ensures that this concatenation cannot feasibly be derived from the outputs of the RNG, and that there are no weird links that can be exploited between the way different chunks and structures generate in the world.

## Discord
https://discord.gg/Jg7Bun7

## Installation
1. Download and run the [Fabric installer](https://fabricmc.net/use).
    - Click "download installer".
    - Note: this step may vary if you aren't using the vanilla launcher
      or an old version of Minecraft.
    - Follow the installation steps depending on whether you want to install on the client or the server.
1. Download SecureSeed from the [releases page](https://github.com/Earthcomputer/SecureSeed/releases)
   and move it to the mods folder (`.minecraft/mods`).

## Contributing
1. Clone the repository
   ```
   git clone https://github.com/Earthcomputer/SecureSeed
   cd SecureSeed
   ```
1. Generate the Minecraft source code
   ```
   ./gradlew genSources
   ```
    - Note: on Windows, use `gradlew` rather than `./gradlew`.
1. Import the project into your preferred IDE.
    1. If you use IntelliJ (the preferred option), you can simply import the project as a Gradle project.
    1. If you use Eclipse, you need to `./gradlew eclipse` before importing the project as an Eclipse project.
1. Edit the code
1. After testing in the IDE, build a JAR to test whether it works outside the IDE too
   ```
   ./gradlew build
   ```
   The mod JAR may be found in the `build/libs` directory
1. [Create a pull request](https://help.github.com/en/articles/creating-a-pull-request)
   so that your changes can be integrated into SecureSeed
    - Note: for large contributions, create an issue before doing all that
      work, to ask whether your pull request is likely to be accepted
