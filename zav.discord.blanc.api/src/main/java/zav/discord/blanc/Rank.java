package zav.discord.blanc;

/**
 * An enumeration of all possible ranks a user may have.<br>
 * Ranks are used to limit, what types of commands a user can use. For example, a normal user
 * shouldn't be able to use developer commands.
 */
public enum Rank {
  REDDIT,
  USER,
  DEVELOPER,
  ROOT;
}
