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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Consumer;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.impl.AbstractMessage;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.entities.impl.UserImpl;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.core.requests.restaction.MessageAction;

/**
 *
 * @author u/Zavarov
 */
public class OfflineMessage extends AbstractMessage{
    public long id;
    public JDAImpl jda;
    public MessageChannel channel;
    public String content;
    public UserImpl user;
    public OffsetDateTime edited_time;
    public List<Attachment> attachments;
    public Consumer<Message> messages;
    public Consumer<String> actions;
    public GuildImpl guild;
    public OfflineMessage(JDAImpl jda, long id, MessageChannel channel, Consumer<Message> messages, Consumer<String> actions) {
        super(null,null,false);
        this.jda = jda;
        this.id = id;
        this.channel = channel;
        this.messages = messages;
        this.actions = actions;
    }
    public void setGuild(GuildImpl guild){
        this.guild = guild;
    }
    @Override
    public Member getMember(){
        return channel.getType() == ChannelType.TEXT ? getGuild().getMember(user) : null;
    }
    @Override
    public Guild getGuild(){
        return guild != null ? guild : channel.getType() == ChannelType.TEXT ? getTextChannel().getGuild() : null;
    }
    @Override
    public MessageChannel getChannel(){
        return channel;
    }
    public void setAttachments(List<Attachment> attachments){
        this.attachments = attachments;
    }
    @Override
    public List<Attachment> getAttachments(){
        return attachments;
    }
    @Override
    public OffsetDateTime getEditedTime(){
        return edited_time;
    }
    public void setEditedTime(OffsetDateTime edited_time){
        this.edited_time = edited_time;
    }
    @Override
    public TextChannel getTextChannel(){
        return channel.getType() == ChannelType.TEXT ? (TextChannel)channel : null;
    }
    
    @Override
    public User getAuthor(){
        return user;
    }
    
    public void setAuthor(UserImpl user){
        this.user = user;
    }
    
    public void setContent(String content){
        this.content = content;
    }

    @Override
    public String getContentRaw(){
        return content;
    }

    @Override
    protected void unsupported() {
    }

    @Override
    public long getIdLong() {
        return id;
    }
    @Override
    public MessageAction editMessage(CharSequence content){
        actions.accept(content.toString());
        return new MessageAction(jda,null,channel){
            @Override
            public void queue(Consumer<? super Message> success, Consumer<? super Throwable> failure){}
        };
    }
    @Override
    public MessageAction editMessage(Message message){
        messages.accept(message);
        return new MessageAction(jda,null,channel){
            @Override
            public void queue(Consumer<? super Message> success, Consumer<? super Throwable> failure){}
        };
    }
    @Override
    public MessageAction editMessage(MessageEmbed message){
        MessageBuilder builder = new MessageBuilder();
        builder.setEmbed(message);
        messages.accept(builder.build());
        return new MessageAction(jda,null,channel){
            @Override
            public void queue(Consumer<? super Message> success, Consumer<? super Throwable> failure){}
        };
    }
    @Override
    public RestAction<Void> addReaction(String unicode){
        actions.accept(unicode);
        return new RestAction.EmptyRestAction<Void>(jda,null){
            @Override
            public void queue(Consumer<? super Void> success, Consumer<? super Throwable> failure){}
        };
    }
    @Override
    public AuditableRestAction<Void> delete(){
        actions.accept(String.format("deleted %s",id));
        return new AuditableRestAction.EmptyRestAction<>(jda, null);
    }
}
