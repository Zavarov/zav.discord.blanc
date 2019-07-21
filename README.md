# Project Title

The main purpose of this project is to implement an [RSS-like](https://en.wikipedia.org/wiki/RSS) feature in [Discord](https://discordapp.com/) with respect to [Reddit](https://www.reddit.com/) submissions.
This means that with this program, a guild is able to be notified in real-time, whenever a new post in a subreddit was made.

Moreover, it also allows to analyze comments and submission during an interval and plot the activity for each day, to name one feature.

#### api
This module implements the core functionality of the program, with the main features being the communicator and the environment interfaces.
For each shard, a single communicator instance will be created that handels all requests in it. The communicator is built on top of them and allows to communicate between shards.

Additionally, this module also provides templates that show the relevant information about Discord- and Reddit-entities like members and submissions.
#### blanc
This module is the core of the program. It implements the available commands and generates the executable jar.
#### command
This module implements the grammar for commands and generates the frame of the respective classes.
In those frames, the preconditions are checked and variables initialized.

Examples on how those classes can be modified can be found in the [blanc](blanc) module.

#### io
This module is responsible for maintaining all configuration files during runtime.
#### use
This module is a legacy of the previous version and extends the functionality of [api](api).
It implements the communicator and environment interfaces and also includes the Reddit feature.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Installing

In order to install this project, simply execute the maven command:

```
mvn clean install
```

The final executable will be generated in the [blanc](blanc) module.

To then run the program, you need to copy the [models](models) directory and the configuration files in the [templates](templates) folder into the same file the jar is in.

Additionally, you need to replace the credentials in [config.cfg](templates/config.cfg) with your own.

## Built With

* [MontiCore](https://github.com/MontiCore/monticore) - For generating and parsing the commands as well as handling the configuration files.
* [Maven](https://maven.apache.org/) - Dependency Management

## Authors

* **Zavarov**

## Dependencies

* [reddit](https://github.com/Zavarov/reddit)

## License

This project is licensed under the GPLv3 License - see the [LICENSE](LICENSE) file for details


