package zav.discord.blanc.view;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import zav.discord.blanc.Argument;
import zav.discord.blanc.databind.GuildValueObject;
import zav.discord.blanc.databind.RoleValueObject;
import zav.discord.blanc.databind.TextChannelValueObject;

/**
 * Base interface for all functions that are performed over guild.
 */
public interface GuildView {
  GuildValueObject getAbout();
  
  /**
   * Self members allow additional functionality exclusive to oneself, such as changing the profile
   * pic or updating ones nickname.
   *
   * @return A view over the member instance corresponding to this program.
   */
  SelfMemberView getSelfMember();
  
  Collection<RoleView> getRoles();
  
  /**
   * Roles may either be identified by their name or id.
   *
   * @param argument An argument representing the requested role.
   * @return The view over the specified role.
   * @throws NoSuchElementException If none or more than one role matching the argument was found.
   */
  RoleView getRole(Argument argument) throws NoSuchElementException;

  Collection<MemberView> getMembers();
  
  /**
   * Members may be identified by their user name, nickname or id.
   *
   * @param argument An argument representing the requested member.
   * @return The view over the specified member.
   * @throws NoSuchElementException If none or more than one member matching the argument was found.
   */
  MemberView getMember(Argument argument) throws NoSuchElementException;

  Collection<TextChannelView> getTextChannels();
  
  /**
   * Text channels may either be identified by their name or id.
   *
   * @param argument An argument representing the requested text channel.
   * @return The view over the specified text channel.
   * @throws NoSuchElementException If none or more than one text channel matching the argument was
   *                                found.
   */
  TextChannelView getTextChannel(Argument argument);
  
  /**
   * Creates a snapshot of the guild activity at the current time.<br>
   * The activity includes the numbers of messages per minute, the total number of members and the
   * number of members that are currently online.
   */
  void updateActivity();
  
  /**
   * Updates all expressions that can't be used in this guild.<br>
   * Every message that contains at least one forbidden expression is automatically deleted.
   *
   * @param pattern The new pattern matching all forbidden expressions.
   */
  void updateBlacklist(Pattern pattern);
  
  /**
   * Forces the bot to leave this guild.
   */
  void leave();
  
  /**
   * Checks whether the given member is allowed to interact with the role.<br>
   * Relevant e.g. for checking whether a user can assign a specific role.
   *
   * @param member The view over member of this guild.
   * @param role A role of this guild.
   * @return {@code true}, when the member is allowed to assign this role to a user.
   */
  boolean canInteract(MemberView member, RoleValueObject role);
  
  /**
   * Creates an activity chart over this guild. The chart plots the total number of members, the
   * number of members that have been online at a given time as well as the number of messages per
   * minute.<br>
   * Additionally, the activity per minutes in {@code channels} is plotted as well.
   *
   * @param channels A list of text channels in this guild.
   * @return An line chart plotting the recent guild activity.
   */
  BufferedImage getActivity(List<TextChannelValueObject> channels);
}
