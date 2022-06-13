# Blanc

A multipurpose Discord bot specialized on cross-posting Reddit submissions via webhooks.

Its core functionality consists of:
   * **A Blacklist** containing banned words. This list consists of an arbitrary amount of regular expressions. 
   If a message matches at least one of those expressions, the program removes them automatically. 
   This functionality becomes useful, for example, when stopping unwanted advertisements.
   * **A Real-Time Subreddit Feed** over specified subreddits. The program will periodically fetch the latest submissions
   and post them in designated text channels. It is recommended to use webhooks, however, for legacy reasons a direct approach is still supported.

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Authors

* **Zavarov**

## License

This project is licensed under the GPLv3 License - see the [LICENSE](LICENSE) file for details


