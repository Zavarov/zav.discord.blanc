package vartas.discord.blanc.command.config;

command @ config{
    filter @ guild{
             class : FilterCommand
        permission : manage messages
    }
    prefix @ guild{
             class : PrefixCommand
        permission : administrator
    }
    reddit @ guild{
             class : RedditCommand
        permission : manage channels
    }
    tag @ guild{
             class : SelfAssignableRoleCommand
        permission : manage roles
    }
}
