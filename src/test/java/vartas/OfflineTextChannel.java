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

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.io.File;
import java.io.InputStream;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.entities.impl.TextChannelImpl;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.requests.restaction.MessageAction;

/**
 *
 * @author u/Zavarov
 */
public class OfflineTextChannel extends TextChannelImpl{
    public Map<Long,Message> message_map;
    public Deque<Message> history;
    public List<Message> retrieved;
    public Consumer<Message> messages;
    public Consumer<String> actions;
    public OfflineTextChannel(long id, GuildImpl guild, Consumer<Message> messages, Consumer<String> actions) {
        super(id, guild);
        this.messages = messages;
        this.actions = actions;
        this.history = new LinkedList<>();
        this.retrieved = new LinkedList<>();
        this.message_map = new Long2ObjectOpenHashMap<>();
    }
    @Override
    public RestAction<Message> getMessageById(String id){
        return getMessageById(Long.parseLong(id));
    }
    
    @Override
    public RestAction<Message> getMessageById(long id){
        return new RestAction.EmptyRestAction<>(guild.get().getJDA(),message_map.get(id));
    }
    
    @Override
    public MessageAction sendMessage(CharSequence content){
        actions.accept(content.toString());
        MessageAction m;
        return  new MessageAction(guild.get().getJDA(),null,this){
            @Override
            public void queue(Consumer<? super Message> success, Consumer<? super Throwable> failure){}
        };
    }
    
    @Override
    public MessageAction sendMessage(Message message){
        messages.accept(message);
        return  new MessageAction(guild.get().getJDA(),null,this){
            @Override
            public void queue(Consumer<? super Message> success, Consumer<? super Throwable> failure){}
        };
    }
    
    @Override
    public MessageAction sendMessage(MessageEmbed message){
        MessageBuilder builder = new MessageBuilder();
        builder.setEmbed(message);
        messages.accept(builder.build());
        return  new MessageAction(guild.get().getJDA(),null,this){
            @Override
            public void queue(Consumer<? super Message> success, Consumer<? super Throwable> failure){}
        };
    }
    @Override
    public MessageAction sendFile(File file){
        actions.accept(file.getName());
        return new MessageAction(guild.get().getJDA(),null,this){
            @Override
            public void queue(Consumer<? super Message> success, Consumer<? super Throwable> failure){}
        };
    }
    @Override
    public MessageAction sendFile(InputStream data, String fileName){
        actions.accept(fileName);
        return new MessageAction(guild.get().getJDA(),null,this){
            @Override
            public void queue(Consumer<? super Message> success, Consumer<? super Throwable> failure){}
        };
    }
    @Override
    public MessageHistory getHistory(){
        return new MessageHistory(this){
            @Override
            public RestAction<List<Message>> retrievePast(int amount){
                List<Message> buffer = new LinkedList<>();
                for(int i = 0 ; i < Math.min(amount,OfflineTextChannel.this.history.size()) ; ++i){
                    buffer.add(OfflineTextChannel.this.history.pop());
                }
                retrieved.addAll(buffer);
                retrieved.sort( (i,j) -> i.getCreationTime().compareTo(j.getCreationTime()));
                return new RestAction.EmptyRestAction<>(guild.get().getJDA(),buffer);
            }
            @Override
            public List<Message> getRetrievedHistory(){
                return retrieved;
            }
        };
    }
    public void addMessage(Message message){
        message_map.put(message.getIdLong(), message);
        history.push(message);
    }
}
