package vartas.discord.blanc.command;

group vartas.discord.blanc.guild {
    command kick requires Guild {
             class : KickClass
        permission : BAN_MEMBERS
         parameter : Member member
              rank : User, Developer
    }
}