# Bot

This project provides the core functionality of a [Discord](https://discordapp.com/) bot.

The primary features consist of:
* Real-Time [Reddit](https://www.reddit.com/) feed
* Self-Assignable roles and role groups
* Automatic message removal based on [regular expressions](https://en.wikipedia.org/wiki/Regular_expression)
* Multi-Pages Messages backed by embeds.

## Getting started

In order to use this bot, one must provide an implementation of the CommandBuilder, in order to recognize and execute
the commands, and a parser that transforms the text files with the guild configurations into instances of BotGuild.

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Authors

* **Zavarov**

## Dependencies:

This project requires at least **Java 8**.  
Due to an internal dependency of [MontiCore](https://github.com/MontiCore/monticore), we can't use a newer version of [Guava](https://github.com/google/guava) than **23.0**.
 * **chart**
   * Version: **1.1.6**
   * [Github](https://github.com/Zavarov/chart)
 * **Evo Inflector**
   * Version: **1.2.2**
   * [Github](https://github.com/atteo/evo-inflector)
 * **JDA**
   * Version: **4.0.0_53**
   * [Github](https://github.com/DV8FromTheWorld/JDA)
 * **reddit**
   * Version: **2.2**
   * [Github](https://github.com/Zavarov/reddit)
 
## Plugins:
 * **Apache Maven JavaDoc Plugin**
   * Version: **3.1.1**
   * [Github](https://github.com/apache/maven-javadoc-plugin)
 * **Apache Maven Source Plugin**
   * Version: **3.1.0**
   * [Github](https://github.com/apache/maven-source-plugin)
## Test Dependencies:
 * **AssertJ**
   * Version: **3.12.2**
   * [Github](https://github.com/joel-costigliola/assertj-core)
 * **JUnit**
   * Version: **4.12**
   * [Github](https://github.com/junit-team/junit4)

## License

This project is licensed under the GPLv3 License - see the [LICENSE](LICENSE) file for details


