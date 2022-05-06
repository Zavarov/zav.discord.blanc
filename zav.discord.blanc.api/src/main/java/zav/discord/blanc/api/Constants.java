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

package zav.discord.blanc.api;

/**
 * A collection of all named objects that may be dependency-injected.
 */
public final class Constants {
  
  private Constants() {}
  
  /**
   * The shard-level injector.
   */
  public static final String SHARD = "shard";
  /**
   * The client-level injector.
   */
  public static final String CLIENT = "client";
  /**
   * An interactive message.
   */
  public static final String SITE = "site";
  /**
   * The user id of the program owner. The owner is always a super-user.
   */
  public static final String OWNER = "owner";
  /**
   * The internal name of this application.
   */
  public static final String BOT_NAME = "botName";
  /**
   * The prefix for all commands which work in any guild and also in private messages.
   */
  public static final String GLOBAL_PREFIX = "globalPrefix";
  /**
   * The total number of shard instances that should be created for this program.
   */
  public static final String SHARD_COUNT = "shardCount";
  /**
   * A Discord invitation link to the development server.
   */
  public static final String INVITE_SUPPORT_SERVER = "inviteSupportServer";
  /**
   * A https link to the wiki of this application, showing a list of all commands and their
   * function.
   */
  public static final String WIKI_URL = "wikiUrl";
  /**
   * The Discord token of this application, required for authentication.
   */
  public static final String DISCORD_TOKEN = "discordToken";
  /**
   * The Pattern cache over all blacklisted expressions.
   */
  public static final String PATTERN = "pattern";
}
