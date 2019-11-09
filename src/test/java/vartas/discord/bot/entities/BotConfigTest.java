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

package vartas.discord.bot.entities;

import org.junit.Test;
import vartas.discord.bot.AbstractBotTest;

import static org.assertj.core.api.Assertions.assertThat;

public class BotConfigTest extends AbstractBotTest {
    @Test
    public void setLongTest(){
        assertThat(configuration.getDiscordShards()).isEqualTo(DISCORD_SHARDS);
        configuration.setType(BotConfig.Type.DISCORD_SHARDS, -1);
        assertThat(configuration.getDiscordShards()).isEqualTo(-1);
    }

    @Test
    public void setStringTest(){
        assertThat(configuration.getBotName()).isEqualTo(BOT_NAME);
        configuration.setType(BotConfig.Type.BOT_NAME, "test");
        assertThat(configuration.getBotName()).isEqualTo("test");
    }

    @Test
    public void getStatusMessageUpdateIntervalTest(){
        assertThat(configuration.getStatusMessageUpdateInterval()).isEqualTo(STATUS_MESSAGE_UPDATE_INTERVAL);
    }

    @Test
    public void getDiscordShardsTest(){
        assertThat(configuration.getDiscordShards()).isEqualTo(DISCORD_SHARDS);
    }

    @Test
    public void getInteractiveMessageLifetimeTest(){
        assertThat(configuration.getInteractiveMessageLifetime()).isEqualTo(INTERACTIVE_MESSAGE_LIFETIME);
    }

    @Test
    public void getActivityUpdateIntervalTest(){
        assertThat(configuration.getActivityUpdateInterval()).isEqualTo(ACTIVITY_UPDATE_INTERVAL);
    }

    @Test
    public void getInviteSupportServerTest(){
        assertThat(configuration.getInviteSupportServer()).isEqualTo(INVITE_SUPPORT_SERVER);
    }

    @Test
    public void getBotNameTest(){
        assertThat(configuration.getBotName()).isEqualTo(BOT_NAME);
    }

    @Test
    public void getGlobalPrefixTest(){
        assertThat(configuration.getGlobalPrefix()).isEqualTo(GLOBAL_PREFIX);
    }

    @Test
    public void getWikiLinkTest(){
        assertThat(configuration.getWikiLink()).isEqualTo(WIKI_LINK);
    }

    @Test
    public void getImageWidthTest(){
        assertThat(configuration.getImageWidth()).isEqualTo(IMAGE_WIDTH);
    }

    @Test
    public void getImageHeightTest(){
        assertThat(configuration.getImageHeight()).isEqualTo(IMAGE_HEIGHT);
    }

    @Test
    public void getDiscordTokenTest(){
        assertThat(configuration.getDiscordToken()).isEqualTo(DISCORD_TOKEN);
    }

    @Test
    public void getRedditAccountTest(){
        assertThat(configuration.getRedditAccount()).isEqualTo(REDDIT_ACCOUNT);
    }

    @Test
    public void getRedditIdTest(){
        assertThat(configuration.getRedditId()).isEqualTo(REDDIT_ID);
    }

    @Test
    public void getRedditSecretTest(){
        assertThat(configuration.getRedditSecret()).isEqualTo(REDDIT_SECRET);
    }
}
