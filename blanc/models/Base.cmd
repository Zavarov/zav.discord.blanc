package vartas.discord.blanc.command.base;

command {
    activity @ guild{
             class : ActivityCommand
    }
    assign @ guild{
             class : AssignCommand
         parameter : role:role
    }
    commenter{
             class : CommenterChartCommand
         parameter : subreddit:string, from:date, to:date, interval:interval
    }
    comments{
             class : CommentChartCommand
         parameter : subreddit:string, from:date, to:date, interval:interval
    }
    flairs{
             class : FlairPlotCommand
         parameter : subreddit:string, from:date, to:date
    }
    help{
             class : HelpCommand
    }
    invite{
             class : InviteCommand
    }
    math{
             class : MathCommand
         parameter : expression:expression
    }
    nsfw{
             class : NsfwChartCommand
         parameter : subreddit:string, from:date, to:date, interval:interval
    }
    ping{
             class : PingCommand
    }
    role @ guild{
             class : RoleCommand
         parameter : role:role
    }
    guild @ guild{
             class : GuildCommand
    }
    spoiler{
             class : SpoilerChartCommand
         parameter : subreddit:string, from:date, to:date, interval:interval
    }
    stats{
             class : StatisticsTableCommand
         parameter : subreddit:string, from:date, to:date
    }
    submissions{
             class : SubmissionChartCommand
         parameter : subreddit:string, from:date, to:date, interval:interval
    }
    submitter{
             class : SubmitterChartCommand
         parameter : subreddit:string, from:date, to:date, interval:interval
    }
    support{
             class : SupportCommand
    }
    tags{
             class : TagPlotCommand
         parameter : subreddit:string, from:date, to:date
    }
    member @ guild{
             class : MemberCommand
         parameter : member:member
    }
}
