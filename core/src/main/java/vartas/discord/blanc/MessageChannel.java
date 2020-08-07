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

import vartas.discord.blanc.factory.MessageEmbedFactory;
import vartas.discord.blanc.factory.MessageFactory;
import vartas.discord.blanc.factory.TitleFactory;
import vartas.reddit.Submission;
import vartas.reddit.Subreddit;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.Instant;
import java.util.Optional;

public abstract class MessageChannel extends MessageChannelTOP{

    /**
     * Wraps the {@link Submission} around a {@link Message} and submits them to the Discord server.
     * @param submission the content of the submitted {@link Message}
     */
    @Override
    public void send(@Nonnull Subreddit subreddit, @Nonnull Submission submission) {
        MessageEmbed messageEmbed = MessageEmbedFactory.create();

        messageEmbed.setTitle(submission.getQualifiedTitle(), submission.getPermaLink());
        messageEmbed.setAuthor("source", submission.getUrl());
        messageEmbed.setTimestamp(Optional.of(submission.getCreated()));

        if(submission.getNsfw()) {
            messageEmbed.setColor(Color.RED);
        }else if(submission.getSpoiler()) {
            messageEmbed.setColor(Color.BLACK);
        }else {
            messageEmbed.setColor(new Color(submission.getAuthor().hashCode()));
            messageEmbed.setThumbnail(submission.getThumbnail());
            messageEmbed.setContent(submission.getContent());
        }

        Message message = MessageFactory.create(0, Instant.now(), null);
        message.setMessageEmbed(messageEmbed);
        message.setContent(String.format("New submission from %s in `r/%s`:\n\n<%s>", submission.getAuthor(), subreddit.getName(), submission.getShortLink()));

        send(message);
    }

    /**
     * Wraps the {@link MessageEmbed} around a {@link Message} and submits them to the Discord server.
     * @param messageEmbed the content of the submitted {@link Message}
     */
    @Override
    public void send(@Nonnull MessageEmbed messageEmbed) {
        Message message = MessageFactory.create(0, Instant.now(), null);

        message.setMessageEmbed(Optional.of(messageEmbed));

        send(message);
    }

    @Override
    public void send(@Nonnull Guild guild) {
        send(guild.toMessageEmbed());
    }

    @Override
    public void send(@Nonnull Role role) {
        send(role.toMessageEmbed());
    }

    @Override
    public void send(@Nonnull Member member) {
        send(member.toMessageEmbed());
    }

    @Override
    public void send(@Nonnull Object object) {
        Message message = MessageFactory.create(0, Instant.now(), null);

        message.setContent(object.toString());

        send(message);
    }

    public void send(@Nonnull String format, @Nonnull Object... arguments){
        send(String.format(format, arguments));
    }
}
