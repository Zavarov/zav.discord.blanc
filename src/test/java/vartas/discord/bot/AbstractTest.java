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
import net.dv8tion.jda.api.Permission;
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
import org.junit.BeforeClass;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    protected static String messageContent = "b.message";

    protected static JDAImpl jda;
    protected static GuildImpl guild;
    protected static TextChannelImpl channel;
    protected static RoleImpl role;
    protected static SelfUserImpl user;
    protected static MemberImpl member;
    protected static Message message;

    protected static Map<String, GuildImpl> guildMap;
    protected static Map<String, TextChannelImpl> channelMap;
    protected static Map<String, RoleImpl> roleMap;
    protected static Map<String, UserImpl> userMap;
    protected static Map<String, MemberImpl> memberMap;
    protected static Map<String, Message> messageMap;


    @BeforeClass
    public static void initJda(){
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
            @Override
            public void shutdown(){}
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

        message = new DataMessage(false, messageContent, null, null){
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
            public ChannelType getChannelType(){
                return ChannelType.TEXT;
            }
            @Nonnull
            @Override
            public MessageChannel getChannel(){
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
        user = new SelfUserImpl(userId, jda);
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

        jda.setSelfUser(user);
        guild.setPublicRole(role);
        role.setRawPermissions(Permission.ALL_PERMISSIONS);
    }
}
