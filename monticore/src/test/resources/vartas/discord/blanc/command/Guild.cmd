package vartas.discord.blanc.command;

group vartas.discord.blanc.guild {
    command kick requires Guild {
             class : KickClass
        permission : BAN_MEMBERS
         parameter : Member member
              rank : User, Developer
    }

    command activity requires Guild {
             class : ActivityClass
         parameter : TextChannel channel+
              rank : User
    }

    command info requires Guild {
             class : InfoClass
         parameter : Member member?
              rank : User
    }
}