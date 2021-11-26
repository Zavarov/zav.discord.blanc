package zav.discord.blanc.view;

/**
 * Base interface for all functions that are performed over guild messages.
 */
public interface GuildMessageView extends MessageView {
  GuildView getGuild();
  
  @Override
  TextChannelView getMessageChannel();
  
  @Override
  MemberView getAuthor();
}
