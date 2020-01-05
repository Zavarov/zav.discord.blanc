#Bot

This project provides the core functionality of a Discord bot. The main features are:
 * **Configuration files** for every guild the bot is in. This is done to retain the setup for each guild, even after the program has been restarted. It is possible to customize:
   * **A Local Guild Prefix** in addition to the global prefix, it is possible to define a guild prefix for all commands.
   * **Banned Words** every newly made message is matched against the regular expression defined by the blacklist. Every message that successfully matches is removed, effectively banning certain words.
   * **Subreddit Feed** a subreddit feed that posts newly made submissions in designated text channels.
   * **Self-Assignable Roles** a way to allow normal members to acquire specified roles. In addition, it is also possible to group those roles together, so that it is only possible to have one role in a group at a time.
whenever a member tries to get a role from a group, but already has one from exactly that group, the previous role is replaced by the new role.

 * **A Real-Time Reddit feed**, which periodically pulls the latest submissions and posts them in a designated text channel.  
It takes at most two minutes for a submission to be posted, after it was submitted on Reddit. One minute is because we only check for submissions every minute, and so it possible that a submission was made just after the period ended, meaning that we have to wait wait for the next one to start.  
The second minute is due to an artificial restriction. A submission has to be at least one minute old, before we post them in a text channel. This is due to the human factor, meaning that it likely that the author hasn't flaired the submission upon submission. The additional minute provides them with enough time to do it afterwards.  
The program will attempt to filter the submissions appropriately, meaning that submissions that have been tagged as NSFW will only be posted in text channels marked as such, for example. Additionally, in such a case, no preview would be shown. However, due to the human factor this is impossible to guarantee, hence why it is recommended to pre-emptively mark all text channels as NSFW who are targeted by a feed.
 * **Custom Commands**, by providing a Command interface. Every command simply has to implement it and can then be integrated into the program.
 * **An Internal Permission System**, since the Permissions used by Discord are not always enough to restrict commands only to a selected group of people.

## Command Builder

The Command Builder interface is used to integrate custom commands into the program. The only function it provides is a build method, which provides the user with the prefix-free content and a reference to the Message instance, which can then be used to create the respective command.

One of the easiest ways to implement this transformation is by defining a table, which maps the message string to a command.

## Command

The Command interface is the core of the custom commands. It only contains a single method, which is used when executing the command, which takes the message that triggered the command, and the shard the command is in, as an argument.

In order to access the internal variables of the program, it is recommended to combine the interface with the visitor pattern.

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Authors

* **Zavarov**

## Dependencies:

This project requires at least **Java 11**.  
 * **chart**
   * Version: **1.1.9**
   * [Github](https://github.com/Zavarov/chart)
 * **Evo Inflector**
   * Version: **1.2.2**
   * [Github](https://github.com/atteo/evo-inflector)
 * **JDA**
   * Version: **4.0.0_70**
   * [Github](https://github.com/DV8FromTheWorld/JDA)
 * **reddit**
   * Version: **2.4**
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


