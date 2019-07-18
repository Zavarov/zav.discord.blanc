package vartas.discord.bot.exec.example;

command @ example{
    test @ guild{
             class : TestCommand
        permission : administrator, manage messages
         parameter : g:guild, d:date
              rank : root, dev
    }
}