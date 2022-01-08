package zav.discord.blanc.runtime.internal;

import zav.discord.blanc.command.Commands;
import zav.discord.blanc.runtime.command.core.ActivityCommand;
import zav.discord.blanc.runtime.command.core.AssignCommand;
import zav.discord.blanc.runtime.command.core.GuildInfoCommand;
import zav.discord.blanc.runtime.command.core.HelpCommand;
import zav.discord.blanc.runtime.command.core.InviteCommand;
import zav.discord.blanc.runtime.command.core.MathCommand;
import zav.discord.blanc.runtime.command.core.MemberInfoCommand;
import zav.discord.blanc.runtime.command.core.PingCommand;
import zav.discord.blanc.runtime.command.core.RoleInfoCommand;
import zav.discord.blanc.runtime.command.core.SupportCommand;
import zav.discord.blanc.runtime.command.dev.AvatarCommand;
import zav.discord.blanc.runtime.command.dev.FailsafeCommand;
import zav.discord.blanc.runtime.command.dev.KillCommand;
import zav.discord.blanc.runtime.command.dev.LeaveCommand;
import zav.discord.blanc.runtime.command.dev.NicknameCommand;
import zav.discord.blanc.runtime.command.dev.RankCommand;
import zav.discord.blanc.runtime.command.dev.SayCommand;
import zav.discord.blanc.runtime.command.dev.StatusCommand;
import zav.discord.blanc.runtime.command.mod.BlacklistCommand;
import zav.discord.blanc.runtime.command.mod.ConfigurationCommand;
import zav.discord.blanc.runtime.command.mod.PrefixCommand;
import zav.discord.blanc.runtime.command.mod.RedditCommand;
import zav.discord.blanc.runtime.command.mod.RoleCommand;
import zav.discord.blanc.runtime.command.mod.legacy.RedditCommandLegacy;

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
    Commands.bind("dev.failsafe", FailsafeCommand.class);
    Commands.bind("dev.kill", KillCommand.class);
    Commands.bind("dev.leave", LeaveCommand.class);
    Commands.bind("dev.nickname", NicknameCommand.class);
    Commands.bind("dev.rank", RankCommand.class);
    Commands.bind("dev.say", SayCommand.class);
    Commands.bind("dev.status", StatusCommand.class);
  }
}
