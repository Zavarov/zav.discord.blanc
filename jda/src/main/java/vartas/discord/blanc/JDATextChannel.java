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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vartas.discord.blanc.$factory.TextChannelFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class JDATextChannel extends TextChannel{
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    @Nonnull
    private final net.dv8tion.jda.api.entities.TextChannel textChannel;
    @Nonnull
    private JDATextChannel(@Nonnull net.dv8tion.jda.api.entities.TextChannel textChannel){
        this.textChannel = textChannel;
    }

    @Override
    public Message getMessages(Long key) throws ExecutionException{
        return getMessages(key, () -> JDAMessage.create(textChannel.retrieveMessageById(key).complete()));
    }

    @Override
    public Webhook getWebhooks(String key) throws ExecutionException{
        try {
            return getWebhooks(key, () -> {
                List<net.dv8tion.jda.api.entities.Webhook> webhooks = textChannel.retrieveWebhooks().complete();

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
        }
    }

    public static TextChannel create(net.dv8tion.jda.api.entities.TextChannel jdaTextChannel){
        TextChannel textChannel = TextChannelFactory.create(
                () -> new JDATextChannel(jdaTextChannel),
                jdaTextChannel.getIdLong(),
                jdaTextChannel.getName()
        );

        if(jdaTextChannel.canTalk()) {
            jdaTextChannel.retrieveWebhooks().complete().forEach(webhook ->
                textChannel.putWebhooks(webhook.getName(), JDAWebhook.create(webhook))
            );
        }

        return textChannel;
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

    @Override
    public String getAsMention(){
        return textChannel.getAsMention();
    }
}
