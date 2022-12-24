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

package zav.discord.blanc.runtime.internal;

import java.util.List;
import java.util.Optional;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import zav.discord.blanc.api.Site;

/**
 * Utility class for managing all subreddit-related tasks.
 */
public abstract class SubredditUtils {
  /**
   * The name of all webhook responsible for posting new Reddit submissions.
   */
  public static final String WEBHOOK = "Reddit";
  
  private SubredditUtils() {}
  
  /**
   * Creates a list of pages over all provided subreddit names. Each page contains at most ten
   * entries. Each entry contains both the subreddit name, as well as its position in the
   * database.<br>
   * The title of each page is {@code Subreddit Feeds}.
   *
   * @param subreddits A list of subreddit names.
   * @return An immutable list of pages.
   */
  public static List<Site.Page> getPages(List<String> subreddits) {
    Site.Page.Builder builder = new Site.Page.Builder("Subreddit Feeds");
    builder.setItemsPerPage(10);
    
    for (int i = 0; i < subreddits.size(); ++i) {
      builder.add("`[{0}]` r/{1}\n", i, subreddits.get(i));
    }
    
    return builder.build();
  }
  
  /**
   * Returns the first webhook in the given text channel which was created by this program.
   *
   * @param channel A text channel visible to this program.
   * @return An {@link Optional} containing the first valid webhook or {@link Optional#empty()}, if
   *         no such webhook exists.
   */
  public static Optional<Webhook> getWebhook(TextChannel channel) {
    return channel.retrieveWebhooks()
    .complete()
    .stream()
    .filter(e -> e.getOwner().getIdLong() == channel.getGuild().getSelfMember().getIdLong())
    .findFirst();
  }
  
  /**
   * Returns the first webhook in the given text channel which was created by this program. If no
   * such webhook is found, a new webhook is created.
   *
   * @param channel A text channel visible to this program.
   * @param name The name of the to-be-created webhook.
   * @return Either the first valid webhook, or the newly created webhook.
   * @see #getWebhook(TextChannel)
   */
  public static Webhook getWebhook(TextChannel channel, String name) {
    return getWebhook(channel).orElseGet(() -> channel.createWebhook(name).complete());
  }
}
