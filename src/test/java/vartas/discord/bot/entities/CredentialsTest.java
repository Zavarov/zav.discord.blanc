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

import org.junit.Before;
import org.junit.Test;
import vartas.discord.bot.AbstractTest;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

public class CredentialsTest extends AbstractTest {
    static int STATUS_MESSAGE_UPDATE_INTERVAL = 10;
    static int INTERACTIVE_MESSAGE_LIFETIME = 12;
    static int ACTIVITY_UPDATE_INTERVAL = 13;
    static int DISCORD_SHARDS = 2;
    static String INVITE_SUPPORT_SERVER = "INVITE_SUPPORT_SERVER";
    static String BOT_NAME = "BOT_NAME";
    static String GLOBAL_PREFIX = "GLOBAL_PREFIX";
    static String WIKI_LINK = "WIKI_LINK";
    static int IMAGE_WIDTH = 10;
    static int IMAGE_HEIGHT = 10;
    static String DISCORD_TOKEN = "DISCORD_TOKEN";
    static String REDDIT_ACCOUNT = "REDDIT_ACCOUNT";
    static String REDDIT_ID = "REDDIT_ID";
    static String REDDIT_SECRET = "REDDIT_SECRET";
    Credentials credentials;

    @Before
    public void setUp(){
        credentials = new Credentials();
        credentials.setType(Credentials.IntegerType.STATUS_MESSAGE_UPDATE_INTERVAL, STATUS_MESSAGE_UPDATE_INTERVAL);
        credentials.setType(Credentials.IntegerType.INTERACTIVE_MESSAGE_LIFETIME, INTERACTIVE_MESSAGE_LIFETIME);
        credentials.setType(Credentials.IntegerType.ACTIVITY_UPDATE_INTERVAL, ACTIVITY_UPDATE_INTERVAL);
        credentials.setType(Credentials.IntegerType.DISCORD_SHARDS, DISCORD_SHARDS);
        credentials.setType(Credentials.StringType.INVITE_SUPPORT_SERVER, INVITE_SUPPORT_SERVER);
        credentials.setType(Credentials.StringType.BOT_NAME, BOT_NAME);
        credentials.setType(Credentials.StringType.GLOBAL_PREFIX, GLOBAL_PREFIX);
        credentials.setType(Credentials.StringType.WIKI_LINK, WIKI_LINK);
        credentials.setType(Credentials.IntegerType.IMAGE_WIDTH, IMAGE_WIDTH);
        credentials.setType(Credentials.IntegerType.IMAGE_HEIGHT, IMAGE_HEIGHT);
        credentials.setType(Credentials.StringType.DISCORD_TOKEN, DISCORD_TOKEN);
        credentials.setType(Credentials.StringType.REDDIT_ACCOUNT, REDDIT_ACCOUNT);
        credentials.setType(Credentials.StringType.REDDIT_ID, REDDIT_ID);
        credentials.setType(Credentials.StringType.REDDIT_SECRET, REDDIT_SECRET);
    }

    @Test(expected=IllegalArgumentException.class)
    public void setMalformedIntegerTest(){
        credentials.setType(Credentials.IntegerType.ACTIVITY_UPDATE_INTERVAL, 0);
    }

    @Test
    public void setIntegerTest(){
        assertThat(credentials.getActivityUpdateInterval()).isEqualTo(ACTIVITY_UPDATE_INTERVAL);
        credentials.setType(Credentials.IntegerType.ACTIVITY_UPDATE_INTERVAL, 4711);
        assertThat(credentials.getActivityUpdateInterval()).isEqualTo(4711);
    }

    @Test
    public void setStringTest(){
        assertThat(credentials.getBotName()).isEqualTo(BOT_NAME);
        credentials.setType(Credentials.StringType.BOT_NAME, "test");
        assertThat(credentials.getBotName()).isEqualTo("test");
    }

    @Test
    public void getStatusMessageUpdateIntervalTest(){
        assertThat(credentials.getStatusMessageUpdateInterval()).isEqualTo(STATUS_MESSAGE_UPDATE_INTERVAL);
    }

    @Test
    public void getInteractiveMessageLifetimeTest(){
        assertThat(credentials.getInteractiveMessageLifetime()).isEqualTo(INTERACTIVE_MESSAGE_LIFETIME);
    }

    @Test
    public void getDiscordShardsTest(){
        assertThat(credentials.getDiscordShards()).isEqualTo(DISCORD_SHARDS);
    }

    @Test
    public void getActivityUpdateIntervalTest(){
        assertThat(credentials.getActivityUpdateInterval()).isEqualTo(ACTIVITY_UPDATE_INTERVAL);
    }

    @Test
    public void getInviteSupportServerTest(){
        assertThat(credentials.getInviteSupportServer()).isEqualTo(INVITE_SUPPORT_SERVER);
    }

    @Test
    public void getBotNameTest(){
        assertThat(credentials.getBotName()).isEqualTo(BOT_NAME);
    }

    @Test
    public void getGlobalPrefixTest(){
        assertThat(credentials.getGlobalPrefix()).isEqualTo(GLOBAL_PREFIX);
    }

    @Test
    public void getWikiLinkTest(){
        assertThat(credentials.getWikiLink()).isEqualTo(WIKI_LINK);
    }

    @Test
    public void getImageWidthTest(){
        assertThat(credentials.getImageWidth()).isEqualTo(IMAGE_WIDTH);
    }

    @Test
    public void getImageHeightTest(){
        assertThat(credentials.getImageHeight()).isEqualTo(IMAGE_HEIGHT);
    }

    @Test
    public void getDiscordTokenTest(){
        assertThat(credentials.getDiscordToken()).isEqualTo(DISCORD_TOKEN);
    }

    @Test
    public void getRedditAccountTest(){
        assertThat(credentials.getRedditAccount()).isEqualTo(REDDIT_ACCOUNT);
    }

    @Test
    public void getRedditIdTest(){
        assertThat(credentials.getRedditId()).isEqualTo(REDDIT_ID);
    }

    @Test
    public void getRedditSecretTest(){
        assertThat(credentials.getRedditSecret()).isEqualTo(REDDIT_SECRET);
    }

    @Test
    public void visitorTest(){
        Credentials.Visitor visitor = new Visitor();
        credentials.accept(visitor);
        visitor = new EmptyVisitor();
        credentials.accept(visitor);
    }

    private static class EmptyVisitor implements Credentials.Visitor{}

    private class Visitor implements Credentials.Visitor{
        @Override
        public void visit(@Nonnull Credentials credentials){
            assertThat(credentials).isEqualTo(CredentialsTest.this.credentials);
        }
        @Override
        public void traverse(@Nonnull Credentials credentials){
            assertThat(credentials).isEqualTo(CredentialsTest.this.credentials);
        }
        @Override
        public void endVisit(@Nonnull Credentials credentials){
            assertThat(credentials).isEqualTo(CredentialsTest.this.credentials);
        }
    }
}
