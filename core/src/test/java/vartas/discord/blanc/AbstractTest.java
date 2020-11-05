/*
 * Copyright (c) 2020 Zavarov
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

package vartas.discord.blanc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import vartas.discord.blanc.$json.JSONGuild;
import vartas.discord.blanc.$json.JSONRole;
import vartas.discord.blanc.$json.JSONTextChannel;
import vartas.discord.blanc.$json.JSONWebhook;
import vartas.discord.blanc.io.$json.JSONCredentials;
import vartas.discord.blanc.io.Credentials;
import vartas.discord.blanc.mock.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class AbstractTest {
    public static Path RESOURCES = Paths.get("src","test","resources");
    public static Credentials credentials;

    @BeforeAll
    public static void setUpAll() throws IOException {
        credentials = JSONCredentials.fromJson(new Credentials(), RESOURCES.resolve("credentials.json"));
        JSONCredentials.CREDENTIALS = credentials;
    }
    public ShardMock shard;
    public GuildMock guild;
    public MemberMock member;
    public TextChannelMock textChannel;
    public WebhookMock webhook;
    public RoleMock role;
    public SelfMemberMock selfMember;
    public SelfUser selfUser;

    public UserMock user;
    public PrivateChannelMock privateChannel;

    /**
     * Shard
     *      - SelfUser [70, "SelfUser"]
     *      - User [0, "User"]
     *          - PrivateChannel [5, "PrivateChannel"]
     *      - Guild [10, "Guild"]
     *          - Role [20, "Purple"]
     *          - TextChannel [30, "Reddit"]
     *              - Webhook [40, "Webhook]
     *              - Member [50, "Member]
     *                  - PrivateChannel [5, "PrivateChannel"]
     *              - SelfMember [60, "SelfMember"]
     */
    @BeforeEach
    private void setUpDiscord() throws IOException {
        shard = new ShardMock();
        guild = (GuildMock) JSONGuild.fromJson(new GuildMock(), 10);
        role = (RoleMock) JSONRole.fromJson(new RoleMock(), guild, 20);
        textChannel = (TextChannelMock) JSONTextChannel.fromJson(new TextChannelMock(), guild, 30);
        webhook = (WebhookMock) JSONWebhook.fromJson(new WebhookMock(), guild, 40);
        privateChannel = new PrivateChannelMock(5, "PrivateChannel");
        selfMember = new SelfMemberMock(60, "SelfMember");
        selfUser = new SelfUserMock(70, "SelfUser");

        user = new UserMock(0, "User");
        member = new MemberMock(50, "Member");

        shard.selfUser = selfUser;
        shard.guilds.put(guild.getId(), guild);
        guild.members.put(member.getId(), member);
        guild.roles.put(role.getId(), role);
        guild.channels.put(textChannel.getId(), textChannel);
        guild.selfMember = selfMember;
        textChannel.webhooks.put(webhook.getId(), webhook);
        user.privateChannel = privateChannel;
        member.privateChannel = privateChannel;
    }
}
