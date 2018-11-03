/*
 * Copyright (C) 2018 u/Zavarov
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
package vartas;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.entities.impl.MemberImpl;
import net.dv8tion.jda.core.entities.impl.PrivateChannelImpl;
import net.dv8tion.jda.core.entities.impl.RoleImpl;
import net.dv8tion.jda.core.entities.impl.UserImpl;
import net.dv8tion.jda.core.managers.GuildController;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.core.requests.restaction.MessageAction;
import vartas.discordbot.DiscordBot;
import vartas.offlinejraw.OfflineNetworkAdapter;
import vartas.xml.XMLConfig;

/**
 *
 * @author u/Zavarov
 */
public class OfflineInstance {
    
    public DiscordBot bot;
    public JDAImpl jda;
    public XMLConfig config;
    
    public UserImpl user, root, developer;
    public OfflineSelfUser self;
    public PrivateChannelImpl user_channel, self_channel;
    
    public GuildImpl guild;
    public OfflineTextChannel channel1,channel2,channel3;
    public RoleImpl role1,role2,role3,public_role;
    
    public MemberImpl self_member;
    public MemberImpl member,root_member,developer_member;
    
    public OfflineMessage private_message, guild_message;
    
    public List<Message> messages;
    public List<String> actions;
    
    public Consumer<Message> consumer_messages;
    public Consumer<String> consumer_actions;
    
    public OfflineInstance(){
        config = XMLConfig.create(new File("src/test/resources/config.xml"));
        jda = new OfflineJDA();
        bot = new DiscordBot(null,jda,config, (c) -> new OfflineNetworkAdapter());
        
        messages = new ArrayList<>();
        actions = new ArrayList<>();
        consumer_messages = (m) -> messages.add(m);
        consumer_actions = (a) -> actions.add(a);
        
        createSelfUser();
        createUser();
        createPrivateChannel();
        createPrivateMessage();
        
        createGuild();
        createTextChannel();
        createRole();
        createSelfMember();
        createMember();
        createGuildMessage();
    }
    
    private void createSelfUser(){
        self = new OfflineSelfUser(10,jda,consumer_actions);
        self.setName("selfuser");
        jda.getUserMap().put(self.getIdLong(), self);
        jda.setSelfUser(self);
    }
    
    private void createUser(){
        user = new UserImpl(20,jda);
        jda.getUserMap().put(user.getIdLong(), user);
        user.setName("user");
        root = new UserImpl(0,jda);
        jda.getUserMap().put(root.getIdLong(), root);
        root.setName("root");
        developer = new UserImpl(2,jda);
        jda.getUserMap().put(developer.getIdLong(), developer);
        developer.setName("developer");
    }
    
    private void createPrivateChannel(){
        user_channel = new PrivateChannelImpl(user.getIdLong(),user){
            @Override
            public MessageAction sendMessage(Message message){
                messages.add(message);
                return  new MessageAction(jda,null,user_channel){
                    @Override
                    public void queue(Consumer<? super Message> success, Consumer<? super Throwable> failure){}
                };
            }
            @Override
            public MessageAction sendMessage(CharSequence message){
                MessageBuilder builder = new MessageBuilder();
                builder.append(message);
                messages.add(builder.build());
                return  new MessageAction(jda,null,self_channel){
                    @Override
                    public void queue(Consumer<? super Message> success, Consumer<? super Throwable> failure){}
                };
            }
            @Override
            public MessageAction sendMessage(MessageEmbed message){
                MessageBuilder builder = new MessageBuilder();
                builder.setEmbed(message);
                messages.add(builder.build());
                return  new MessageAction(jda,null,self_channel){
                    @Override
                    public void queue(Consumer<? super Message> success, Consumer<? super Throwable> failure){}
                };
            }
            @Override
            public MessageAction sendFile(InputStream data, String fileName, Message message){
                actions.add(fileName);
                return new MessageAction(jda,null,user_channel){
                    @Override
                    public void queue(Consumer<? super Message> success, Consumer<? super Throwable> failure){}
                };
            }
        };
        
        self_channel = new PrivateChannelImpl(self.getIdLong(),self){
            @Override
            public MessageAction sendMessage(Message message){
                messages.add(message);
                return  new MessageAction(jda,null,self_channel){
                    @Override
                    public void queue(Consumer<? super Message> success, Consumer<? super Throwable> failure){}
                };
            }
            @Override
            public MessageAction sendMessage(CharSequence message){
                MessageBuilder builder = new MessageBuilder();
                builder.append(message);
                messages.add(builder.build());
                return  new MessageAction(jda,null,self_channel){
                    @Override
                    public void queue(Consumer<? super Message> success, Consumer<? super Throwable> failure){}
                };
            }
            @Override
            public MessageAction sendMessage(MessageEmbed message){
                MessageBuilder builder = new MessageBuilder();
                builder.setEmbed(message);
                messages.add(builder.build());
                return  new MessageAction(jda,null,self_channel){
                    @Override
                    public void queue(Consumer<? super Message> success, Consumer<? super Throwable> failure){}
                };
            }
            @Override
            public MessageAction sendFile(File file){
                actions.add(file.getName());
                return new MessageAction(jda,null,self_channel){
                    @Override
                    public void queue(Consumer<? super Message> success, Consumer<? super Throwable> failure){}
                };
            }
            @Override
            public MessageAction sendFile(InputStream data, String fileName, Message message){
                actions.add(fileName);
                return new MessageAction(jda,null,self_channel){
                    @Override
                    public void queue(Consumer<? super Message> success, Consumer<? super Throwable> failure){}
                };
            }
        };
    }
    
    private void createPrivateMessage(){
        private_message = new OfflineMessage(jda,self.getIdLong(),self_channel,consumer_messages, consumer_actions);
        private_message.setAuthor(self);
        private_message.setContent("private_message");
    }
    
    private void createGuild(){
        guild = new GuildImpl(jda,0){
            @Override
            public RestAction<Void> leave(){
                actions.add(String.format("left %s",guild.getId()));
                return new RestAction.EmptyRestAction<>(jda,null);
            }
            @Override
            public GuildController getController(){
                return new GuildController(guild){
                    @Override
                    public AuditableRestAction<Void> setNickname(Member member, String nickname){
                        actions.add(String.format("nickname of %s changed to %s",member.getEffectiveName(),nickname));
                        return new AuditableRestAction.EmptyRestAction<>(jda,null);
                    }
                };
            }
        };
        guild.setName("guild");
        guild.setAvailable(true);
        jda.getGuildMap().put(guild.getIdLong(), guild);
    }
    
    private void createTextChannel(){
        channel1 = new OfflineTextChannel(1,guild,consumer_messages,consumer_actions);
        channel1.setName("channel1");
        channel1.setNSFW(true);
        guild.getTextChannelsMap().put(channel1.getIdLong(), channel1);
        
        channel2 = new OfflineTextChannel(2,guild,consumer_messages,consumer_actions);
        channel2.setName("channel2");
        channel2.setNSFW(true);
        guild.getTextChannelsMap().put(channel2.getIdLong(), channel2);
        
        channel3 = new OfflineTextChannel(3,guild,consumer_messages,consumer_actions);
        channel3.setName("channel3");
        channel3.setNSFW(true);
        guild.getTextChannelsMap().put(channel3.getIdLong(), channel3);
    }
    
    private void createRole(){
        role1 = new RoleImpl(1,guild);
        role1.setName("role1");
        guild.getRolesMap().put(role1.getIdLong(), role1);
        
        role2 = new RoleImpl(2,guild);
        role2.setName("role2");
        guild.getRolesMap().put(role2.getIdLong(), role2);
        
        role3 = new RoleImpl(3,guild);
        role3.setName("role3");
        guild.getRolesMap().put(role3.getIdLong(), role3);
        
        public_role = new RoleImpl(0,guild);
        public_role.setName("public_role");
        guild.getRolesMap().put(public_role.getIdLong(), public_role);
        guild.setPublicRole(public_role);
        
    }
    
    private void createSelfMember(){
        self_member = new MemberImpl(guild,self);
        self_member.setJoinDate(System.currentTimeMillis());
        self_member.setNickname("self_member");
        self_member.setOnlineStatus(OnlineStatus.ONLINE);
        
        guild.getMembersMap().put(self.getIdLong(), self_member);
        guild.setOwner(self_member);
    }
    
    private void createMember(){
        member = new MemberImpl(guild,user);
        member.setJoinDate(System.currentTimeMillis());
        member.setNickname("member");
        member.setOnlineStatus(OnlineStatus.ONLINE);
        root_member = new MemberImpl(guild,root);
        root_member.setJoinDate(System.currentTimeMillis());
        root_member.setNickname("root");
        root_member.setOnlineStatus(OnlineStatus.ONLINE);
        developer_member = new MemberImpl(guild,developer);
        developer_member.setJoinDate(System.currentTimeMillis());
        developer_member.setNickname("developer");
        developer_member.setOnlineStatus(OnlineStatus.ONLINE);
        
        guild.getMembersMap().put(user.getIdLong(), member);
        guild.getMembersMap().put(root.getIdLong(), root_member);
        guild.getMembersMap().put(developer.getIdLong(), developer_member);
    }
    
    private void createGuildMessage(){
        guild_message = new OfflineMessage(jda,self.getIdLong(),channel1,consumer_messages,consumer_actions);
        guild_message.setAuthor(self);
        guild_message.setContent("guild_message");
    }
}
