/*
 * Copyright (c) 2022 Zavarov.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package zav.discord.blanc.runtime.command.mod;

import static net.dv8tion.jda.api.Permission.MANAGE_CHANNEL;
import static zav.discord.blanc.runtime.internal.DatabaseUtils.getOrCreate;

import java.util.Locale;
import java.util.Objects;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.databind.WebhookEntity;
import zav.discord.blanc.reddit.SubredditObservable;

/**
 * This command links subreddits to Discord channels.
 */
public abstract class AbstractRedditCommand extends AbstractGuildCommand {
  @Inject
  protected SubredditObservable observable;
  
  @Inject
  protected SlashCommandEvent event;
  
  @Inject
  protected TextChannel textChannel;
  
  protected String subreddit;
  protected TextChannel target;
  
  protected AbstractRedditCommand() {
    super(MANAGE_CHANNEL);
  }
  
  @Override
  public void postConstruct() {
    subreddit = Objects.requireNonNull(event.getOption("subreddit")).getAsString().toLowerCase(Locale.ENGLISH);
    target = (TextChannel) (event.getOption("channel") == null ? textChannel : event.getOption("channel").getAsGuildChannel());
  }
}
