package vartas.discord.blanc.command.developer;

command @ dev{
    avatar @ attachment{
             class : AvatarCommand
              rank : dev
    }
    delete @ guild{
             class : DeleteCommand
         parameter : message:message
              rank : dev
    }
    reaction @ guild{
             class : ReactionCommand
         parameter : message:message, reaction:string
              rank : dev
    }
    reddit{
             class : RedditRankCommand
         parameter : user:user
              rank : dev
    }
    failsafe{
             class : FailsafeCommand
              rank : dev
    }
    guild{
             class : GuildCommand
              rank : dev
    }
    kill{
             class : KillCommand
              rank : dev
    }
    leave{
             class : LeaveCommand
         parameter : guild:guild
              rank : dev
    }
    log{
             class : LogCommand
              rank : dev
    }
    nickname @ guild{
             class : NicknameCommand
         parameter : nickname:string
              rank : dev
    }
    onlinestatus{
             class : OnlineStatusCommand
         parameter : status:onlinestatus
              rank : dev
    }
    say{
             class : SayCommand
         parameter : content:string
              rank : dev
    }
    status{
             class : StatusCommand
              rank : dev
    }
}
