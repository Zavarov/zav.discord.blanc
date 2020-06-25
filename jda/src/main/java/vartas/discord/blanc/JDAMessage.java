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

import vartas.discord.blanc.factory.MessageFactory;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JDAMessage extends Message {
    @Nonnull
    public static Message create(net.dv8tion.jda.api.entities.Message message){
        long id = message.getIdLong();
        Instant created = message.getTimeCreated().toInstant();
        User author = JDAUser.create(message.getAuthor());
        Optional<String> content = message.getContentRaw().isEmpty() ? Optional.empty() : Optional.of(message.getContentRaw());
        //TODO
        Optional<MessageEmbed> messageEmbed = Optional.empty();
        List<Attachment> attachments = message.getAttachments().stream().map(JDAAttachment::create).collect(Collectors.toList());

        return MessageFactory.create(JDAMessage::new, id, created, author, content, messageEmbed, attachments);
    }
}
