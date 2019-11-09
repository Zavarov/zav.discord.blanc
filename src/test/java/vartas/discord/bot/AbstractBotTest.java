/*
 * Copyright (c) 2019 Zavarov
 *
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

package vartas.discord.bot;

import net.dv8tion.jda.api.entities.Message;
import org.junit.Before;
import vartas.discord.bot.entities.*;

import java.util.function.Function;

public abstract class AbstractBotTest extends AbstractTest {
    protected static long STATUS_MESSAGE_UPDATE_INTERVAL = 10;
    protected static long DISCORD_SHARDS = 11;
    protected static long INTERACTIVE_MESSAGE_LIFETIME = 12;
    protected static long ACTIVITY_UPDATE_INTERVAL = 13;
    protected static String INVITE_SUPPORT_SERVER = "INVITE_SUPPORT_SERVER";
    protected static String BOT_NAME = "BOT_NAME";
    protected static String GLOBAL_PREFIX = "GLOBAL_PREFIX";
    protected static String WIKI_LINK = "WIKI_LINK";
    protected static long IMAGE_WIDTH = 10;
    protected static long IMAGE_HEIGHT = 10;
    protected static String DISCORD_TOKEN = "DISCORD_TOKEN";
    protected static String REDDIT_ACCOUNT = "REDDIT_ACCOUNT";
    protected static String REDDIT_ID = "REDDIT_ID";
    protected static String REDDIT_SECRET = "REDDIT_SECRET";

    protected BotConfig configuration;
    protected BotRank rank;
    protected BotStatus status;

    @Before
    public void initConfig(){
        configuration = new BotConfig();

        configuration.setType(BotConfig.Type.STATUS_MESSAGE_UPDATE_INTERVAL, STATUS_MESSAGE_UPDATE_INTERVAL);
        configuration.setType(BotConfig.Type.DISCORD_SHARDS, DISCORD_SHARDS);
        configuration.setType(BotConfig.Type.INTERACTIVE_MESSAGE_LIFETIME, INTERACTIVE_MESSAGE_LIFETIME);
        configuration.setType(BotConfig.Type.ACTIVITY_UPDATE_INTERVAL, ACTIVITY_UPDATE_INTERVAL);
        configuration.setType(BotConfig.Type.INVITE_SUPPORT_SERVER, INVITE_SUPPORT_SERVER);
        configuration.setType(BotConfig.Type.BOT_NAME, BOT_NAME);
        configuration.setType(BotConfig.Type.GLOBAL_PREFIX, GLOBAL_PREFIX);
        configuration.setType(BotConfig.Type.WIKI_LINK, WIKI_LINK);
        configuration.setType(BotConfig.Type.IMAGE_WIDTH, IMAGE_WIDTH);
        configuration.setType(BotConfig.Type.IMAGE_HEIGHT, IMAGE_HEIGHT);
        configuration.setType(BotConfig.Type.DISCORD_TOKEN, DISCORD_TOKEN);
        configuration.setType(BotConfig.Type.REDDIT_ACCOUNT, REDDIT_ACCOUNT);
        configuration.setType(BotConfig.Type.REDDIT_ID, REDDIT_ID);
        configuration.setType(BotConfig.Type.REDDIT_SECRET, REDDIT_SECRET);
    }

    @Before
    public void initRank(){
        rank = new BotRank(jda);

        rank.add(user, BotRank.Type.DEVELOPER);
        rank.add(user, BotRank.Type.REDDIT);
    }

    @Before
    public void initStatus(){
        status = new BotStatus();
    }
}
