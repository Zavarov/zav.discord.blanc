package zav.discord.blanc.view;

import zav.discord.blanc.databind.Message;

public interface GuildMessageView extends MessageView{
  GuildView getGuild();
  TextChannelView getMessageChannel();
  MemberView getAuthor();
}
