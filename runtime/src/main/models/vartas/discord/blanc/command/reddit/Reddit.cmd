package vartas.discord.blanc.command.reddit;

import vartas.chart.Interval.Interval;
import java.time.LocalDate.LocalDate;

group reddit {
    command user{
             class : AccountCommand
         parameter : String subreddit, LocalDate from, LocalDate to, String account
              rank : REDDIT
    }
    command table{
             class : SnowflakeTableCommand
         parameter : String subreddit, LocalDate from, LocalDate to, String type
              rank : REDDIT
    }
    command markdown{
             class : MarkdownTableCommand
         parameter : String subreddit, LocalDate from, LocalDate to, String type
              rank : REDDIT
    }
    command submission{
             class : SubmissionCommand
         parameter : String subreddit, LocalDate from, LocalDate to, String type
              rank : REDDIT
    }
    command subreddit{
             class : SubredditCommand
         parameter : String subreddit, LocalDate from, LocalDate to, Interval interval
              rank : Reddit
    }
}
