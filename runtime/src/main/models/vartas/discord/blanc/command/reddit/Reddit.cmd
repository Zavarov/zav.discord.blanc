package vartas.discord.blanc.command.reddit;
prefix reddit;

command {
    subreddit{
             class : "SubredditChartCommand"
         parameter : subreddit:String, from:Date, to:Date, type:String
              rank : Reddit
    }
    snowflake{
             class : "SnowflakeChartCommand"
         parameter : subreddit:String, from:Date, to:Date, interval:Interval, arguments:String+
              rank : Reddit
    }
    table{
             class : "SnowflakeTableCommand"
         parameter : subreddit:String, from:Date, to:Date
              rank : Reddit
    }
    markdown{
             class : "MarkdownTableCommand"
         parameter : subreddit:String, from:Date, to:Date
              rank : Reddit
    }
}
