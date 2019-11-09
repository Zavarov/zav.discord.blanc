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

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.*;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.config.AuthorizationConfig;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import vartas.discord.bot.entities.BotConfig;
import vartas.discord.bot.entities.BotRank;
import vartas.discord.bot.entities.BotStatus;

import javax.annotation.Nonnull;
import java.util.*;

public abstract class AbstractTest {
    protected static long guildId = 0L;
    protected static long channelId = 1L;
    protected static long roleId = 2L;
    protected static long userId = 3L;
    protected static long memberId = userId;
    protected static long messageId = 4L;

    protected static String guildName = "guild";
    protected static String channelName = "channel";
    protected static String roleName = "role";
    protected static String userName = "user";
    protected static String memberNickname = "member";

    protected JDAImpl jda;
    protected GuildImpl guild;
    protected TextChannelImpl channel;
    protected RoleImpl role;
    protected UserImpl user;
    protected MemberImpl member;
    protected Message message;

    protected Map<String, GuildImpl> guildMap;
    protected Map<String, TextChannelImpl> channelMap;
    protected Map<String, RoleImpl> roleMap;
    protected Map<String, UserImpl> userMap;
    protected Map<String, MemberImpl> memberMap;
    protected Map<String, Message> messageMap;


    @Before
    public void initJda(){
        AuthorizationConfig config = new AuthorizationConfig(AccountType.BOT, "12345");

        guildMap = new HashMap<>();
        channelMap = new HashMap<>();
        roleMap = new HashMap<>();
        userMap = new HashMap<>();
        memberMap = new HashMap<>();
        messageMap = new HashMap<>();

        jda = new JDAImpl(config){
            @Override
            public GuildImpl getGuildById(long id){
                return getGuildById(Long.toString(id));
            }
            @Override
            public GuildImpl getGuildById(@Nonnull String id){
                return guildMap.get(id);
            }
            @Nonnull
            @Override
            public List<Guild> getGuildsByName(@Nonnull String name, boolean ignoreCase){
                return Collections.singletonList(guildMap.get(name));
            }
            @Override
            public UserImpl getUserById(long id){
                return getUserById(Long.toString(id));
            }
            @Override
            public UserImpl getUserById(@Nonnull String id){
                return userMap.get(id);
            }
            @Nonnull
            @Override
            public List<User> getUsersByName(@Nonnull String name, boolean ignoreCase){
                return Collections.singletonList(userMap.get(name));
            }
        };

        guild = new GuildImpl(jda, guildId){
            @Override
            public RoleImpl getRoleById(long id){
                return getRoleById(Long.toString(id));
            }
            @Override
            public RoleImpl getRoleById(@Nonnull String id){
                return roleMap.get(id);
            }
            @Nonnull
            @Override
            public List<Role> getRolesByName(@Nonnull String name, boolean ignoreCase){
                return Collections.singletonList(roleMap.get(name));
            }
            @Override
            public TextChannelImpl getTextChannelById(long id){
                return getTextChannelById(Long.toString(id));
            }
            @Override
            public TextChannelImpl getTextChannelById(@Nonnull String id){
                return channelMap.get(id);
            }
            @Nonnull
            @Override
            public List<TextChannel> getTextChannelsByName(@Nonnull String name, boolean ignoreCase){
                return Collections.singletonList(channelMap.get(name));
            }
            @Override
            public MemberImpl getMemberById(long id){
                return getMemberById(Long.toString(id));
            }
            @Override
            public MemberImpl getMemberById(@Nonnull String id){
                return memberMap.get(id);
            }
            @Override
            public List<Member> getMembersByName(@Nonnull String name, boolean ignoreCase){
                return Collections.singletonList(memberMap.get(name));
            }
            @Nonnull
            @Override
            public List<Member> getMembersByEffectiveName(@Nonnull String name, boolean ignoreCase){
                return getMembersByName(name, ignoreCase);
            }
            @Nonnull
            @Override
            public List<Member> getMembersByNickname(String name, boolean ignoreCase){
                return getMembersByName(name, ignoreCase);
            }
            @NotNull
            @Override
            public List<Member> getMembers(){
                return Collections.singletonList(member);
            }
        };

        message = new DataMessage(false, null, null, null){
            @Nonnull
            @Override
            public JDAImpl getJDA(){
                return jda;
            }
            @Nonnull
            @Override
            public GuildImpl getGuild(){
                return guild;
            }
            @Nonnull
            @Override
            public TextChannelImpl getTextChannel(){
                return channel;
            }
            @Nonnull
            @Override
            public String getId(){
                return Long.toString(getIdLong());
            }
            @Nonnull
            @Override
            public User getAuthor(){
                return user;
            }
            @Override
            public long getIdLong(){
                return messageId;
            }
        };

        channel = new TextChannelImpl(channelId, guild){
            @Override
            public RestAction<Message> retrieveMessageById(long id){
                return retrieveMessageById(Long.toString(id));
            }
            @Nonnull
            @Override
            public RestAction<Message> retrieveMessageById(@Nonnull String id){
                return new RestActionImpl<Message>(jda, null){
                    @Override
                    public Message complete(){
                        if(messageMap.containsKey(id))
                            return messageMap.get(id);
                        else
                            throw ErrorResponseException.create(ErrorResponse.UNAUTHORIZED, new Response(0L, Collections.emptySet()));
                    }
                };
            }
        };
        role = new RoleImpl(roleId, guild);
        user = new UserImpl(userId, jda);
        member = new MemberImpl(guild, user);

        guild.setName(guildName);
        channel.setName(channelName);
        role.setName(roleName);
        user.setName(userName);
        member.setNickname(memberNickname);

        guildMap.put(guild.getId(), guild);
        channelMap.put(channel.getId(), channel);
        roleMap.put(role.getId(), role);
        userMap.put(user.getId(), user);
        memberMap.put(member.getId(), member);
        messageMap.put(message.getId(), message);

        guildMap.put(guild.getName(), guild);
        channelMap.put(channel.getName(), channel);
        roleMap.put(role.getName(), role);
        userMap.put(user.getName(), user);
        memberMap.put(member.getNickname(), member);
    }
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
