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

import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vartas.discord.blanc.factory.TextChannelFactory;
import vartas.discord.blanc.json.JSONTextChannel;
import vartas.discord.blanc.json.JSONWebhook;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JDATextChannel extends TextChannel{
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    @Nonnull
    private final net.dv8tion.jda.api.entities.TextChannel textChannel;
    @Nonnull
    private JDATextChannel(@Nonnull net.dv8tion.jda.api.entities.TextChannel textChannel){
        this.textChannel = textChannel;
    }

    @Override
    public Message getMessages(Long key){
        try{
            return getMessages(key, () -> JDAMessage.create(textChannel.retrieveMessageById(key).complete()));
        }catch(ExecutionException e){
            //TODO Internal error
            throw new RuntimeException("Internal error: " + e.getMessage());
        }
    }

    @Override
    public Webhook getWebhooks(String key){
        try {
            return getWebhooks(key, () -> {
                List<net.dv8tion.jda.api.entities.Webhook> webhooks = textChannel.retrieveWebhooks().complete();

                webhooks.stream().forEach(x -> System.out.println(x.getName()));

                return JDAWebhook.create(
                        webhooks.stream()
                                .filter(w -> w.getName().equals(key))
                                .findAny()
                                .orElse(textChannel.createWebhook(key).complete())
                );
            });
        } catch(net.dv8tion.jda.api.exceptions.PermissionException e){
            log.error(Errors.INSUFFICIENT_PERMISSION.toString(), e);
            throw PermissionException.of(Errors.INSUFFICIENT_PERMISSION);
        }catch(ExecutionException e){
            //TODO Internal error
            throw new RuntimeException("Internal error: " + e.getMessage());
        }
    }

    public static TextChannel create(net.dv8tion.jda.api.entities.TextChannel jdaTextChannel, @Nullable JSONObject jsonObject){
        TextChannel textChannel = TextChannelFactory.create(
                () -> new JDATextChannel(jdaTextChannel),
                jdaTextChannel.getIdLong(),
                jdaTextChannel.getName()
        );

        if(jsonObject != null && jdaTextChannel.canTalk()){
            JSONArray jsonSubreddits = jsonObject.optJSONArray(JSONTextChannel.SUBREDDITS);
            if(jsonSubreddits != null)
                for(int i = 0 ; i < jsonSubreddits.length() ; ++i)
                    textChannel.addSubreddits(jsonSubreddits.getString(i));


            JSONObject jsonWebhooks = jsonObject.optJSONObject(JSONTextChannel.WEBHOOKS);
            if(jsonWebhooks != null) {
                try {
                    //Group available webhooks by name
                    Map<String, net.dv8tion.jda.api.entities.Webhook> jdaWebhooks = jdaTextChannel
                            .retrieveWebhooks()
                            .complete()
                            .stream()
                            .collect(Collectors.toMap(net.dv8tion.jda.api.entities.Webhook::getName, Function.identity()));

                    for (String subreddit : jsonWebhooks.keySet()) {
                        JSONWebhook jsonWebhook = JSONWebhook.of(jsonWebhooks.getJSONObject(subreddit));
                        //Try to recover the webhook
                        net.dv8tion.jda.api.entities.Webhook jdaWebhook = jdaWebhooks.getOrDefault(jsonWebhook.getName(), null);

                        if (jdaWebhook != null)
                            textChannel.putWebhooks(subreddit, JDAWebhook.create(jdaWebhook));
                    }
                }catch(InsufficientPermissionException e){
                    LoggerFactory.getLogger(JDATextChannel.class.getSimpleName()).warn("Couldn't retrieve webhooks.", e);
                }
            }
        }

        return textChannel;
    }

    public static TextChannel create(net.dv8tion.jda.api.entities.TextChannel jdaTextChannel){
        return create(jdaTextChannel, null);
    }

    @Override
    public void send(Message message) {
        try {
            textChannel.sendMessage(MessageBuilder.buildMessage(message)).complete();
        } catch(InsufficientPermissionException e){
            throw PermissionException.of(Errors.INSUFFICIENT_PERMISSION);
        }
    }

    @Override
    public void send(byte[] bytes, String qualifiedName) {
        try {
            textChannel.sendFile(bytes, qualifiedName).complete();
        } catch(InsufficientPermissionException e){
            throw PermissionException.of(Errors.INSUFFICIENT_PERMISSION);
        }
    }
}
