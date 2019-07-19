package vartas.discord.blanc.command.reddit;

command @ reddit{
    request{
             class : RequestCommand
         parameter : subreddit:string, from:date, to:date
              rank : reddit
    }
    memory{
             class : MemoryCommand
         parameter : subreddit:string, from:date, to:date
              rank : reddit
    }
}
