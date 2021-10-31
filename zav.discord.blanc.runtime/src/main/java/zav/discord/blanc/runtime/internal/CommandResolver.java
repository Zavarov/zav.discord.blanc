package zav.discord.blanc.runtime.internal;

import zav.discord.blanc.command.Commands;
import zav.discord.blanc.runtime.command.core.HelpCommand;
import zav.discord.blanc.runtime.command.core.InviteCommand;
import zav.discord.blanc.runtime.command.core.MathCommand;
import zav.discord.blanc.runtime.command.core.PingCommand;
import zav.discord.blanc.runtime.command.core.SupportCommand;
import zav.discord.blanc.runtime.command.dev.*;
import zav.discord.blanc.runtime.command.guild.ActivityCommand;
import zav.discord.blanc.runtime.command.guild.AssignCommand;
import zav.discord.blanc.runtime.command.guild.info.GuildInfoCommand;
import zav.discord.blanc.runtime.command.guild.info.MemberInfoCommand;
import zav.discord.blanc.runtime.command.guild.info.RoleInfoCommand;
import zav.discord.blanc.runtime.command.guild.mod.*;
import zav.discord.blanc.runtime.command.guild.mod.legacy.RedditCommandLegacy;

public class CommandResolver {
  public static void init() {
    Commands.bind("help", HelpCommand.class);
    Commands.bind("invite", InviteCommand.class);
    Commands.bind("math", MathCommand.class);
    Commands.bind("ping", PingCommand.class);
    Commands.bind("support", SupportCommand.class);
    
    Commands.bind("guild", GuildInfoCommand.class);
    Commands.bind("member", MemberInfoCommand.class);
    Commands.bind("role", RoleInfoCommand.class);
  
    Commands.bind("activity", ActivityCommand.class);
    Commands.bind("assign", AssignCommand.class);
    
    Commands.bind("mod.blacklist", BlacklistCommand.class);
    Commands.bind("mod.config", ConfigurationCommand.class);
    Commands.bind("mod.prefix", PrefixCommand.class);
    Commands.bind("mod.reddit", RedditCommand.class);
    Commands.bind("mod.role", RoleCommand.class);
    Commands.bind("mod.legacy.reddit", RedditCommandLegacy.class);
  
    Commands.bind("dev.avatar", AvatarCommand.class);
    Commands.bind("dev.delete", DeleteCommand.class);
    Commands.bind("dev.failsafe", FailsafeCommand.class);
    Commands.bind("dev.kill", KillCommand.class);
    Commands.bind("dev.leave", LeaveCommand.class);
    Commands.bind("dev.nickname", NicknameCommand.class);
    Commands.bind("dev.rank", RankCommand.class);
    Commands.bind("dev.react", ReactionCommand.class);
    Commands.bind("dev.say", SayCommand.class);
    Commands.bind("dev.status", StatusCommand.class);
    Commands.bind("dev.user", UserCommand.class);
  }
}
