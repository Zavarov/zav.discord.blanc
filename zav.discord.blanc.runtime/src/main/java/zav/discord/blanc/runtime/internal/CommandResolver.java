package zav.discord.blanc.runtime.internal;

import zav.discord.blanc.api.Commands;
import zav.discord.blanc.runtime.command.core.MathCommand;
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
import zav.discord.blanc.runtime.command.mod.legacy.RedditCommandLegacy;

/**
 * Utility class for mapping each command class to their name.
 */
public class CommandResolver {
  /**
   * Initializes all supported commands.
   */
  public static void init() {
    Commands.bind("math", MathCommand.class);
    Commands.bind("support", SupportCommand.class);
    
    Commands.bind("mod.blacklist", BlacklistCommand.class);
    Commands.bind("mod.config", ConfigurationCommand.class);
    Commands.bind("mod.prefix", PrefixCommand.class);
    Commands.bind("mod.reddit", RedditCommand.class);
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
