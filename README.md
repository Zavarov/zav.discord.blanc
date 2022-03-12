# Blanc

A multipurpose Discord bot specialized on cross-posting Reddit submissions in dedicated text channels.

Its core functionality consists of:
   * **A Local Guild Prefix** in addition to the global prefix, it is possible to define a guild prefix for all commands. 
   Any command either accepts the global or the local prefix. This mitigates the issue of colliding prefixes when using multiple bots.
   * **A Blacklist** containing banned words. This list consists of an arbitrary amount of regular expressions. 
   If a message matches at least one of those expressions, the program removes them automatically. 
   This functionality becomes useful, for example, when stopping unwanted advertisements.
   * **A Real-Time Subreddit Feed** over specified subreddits. The program will periodically fetch the latest submissions
   and post them in designated text channels. It is recommended to use webhooks, however, for legacy reasons a direct approach is still supported.
   * **Self-Assignable Roles** allow normal members to acquire custom roles. Additionally, the program supports grouping those roles,
   meaning at any time, for each group, a user can have at most one role. Requesting a role while already having another role
   in the same group will effectively replace the old with the new role.

Moreover, the program is highly adaptable and allows to easily register new commands by modifying the corresponding model files.
[MontiCore](https://github.com/MontiCore/monticore) will generate the backend and automatically integrate into the rest of the code.
The same workbench is responsible for processing the received messages that will trigger those commands.
In addition to the Discord permissions, an internal rank system allows to further restrict commands to a subset of users.
Unless a user has both the correct permissions and rank, any command that requires both of them will fail.

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [MontiCore](https://github.com/MontiCore/monticore) - Command Generation and Processing

## Authors

* **Zavarov**

## License

This project is licensed under the GPLv3 License - see the [LICENSE](LICENSE) file for details


