package vartas.discord.blanc.command.mod;

command @ mod{
    filter @ guild{
             class : FilterCommand
         parameter : expression:string
        permission : manage messages
    }
    prefix @ guild{
             class : PrefixCommand
         parameter : prefix:string
        permission : administrator
    }
    reddit @ guild{
             class : RedditCommand
         parameter : subreddit:string, textchannel:textchannel
        permission : manage channels
    }
    tag @ guild{
             class : SelfAssignableRoleCommand
         parameter : tag:string, role:role
        permission : manage roles
    }
}
