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

import com.google.common.base.Preconditions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import vartas.discord.blanc.factory.UserFactory;
import vartas.discord.blanc.visitor.ArchitectureVisitor;

import javax.annotation.Nonnull;
import java.util.Optional;

@Nonnull
public class JDAServerHookPoint implements ServerHookPoint {
    @Nonnull
    private final JDA jda;
    public JDAServerHookPoint(@Nonnull JDA jda){
        this.jda = jda;
    }

    @Override
    public void send(@Nonnull MessageChannel messageChannel, @Nonnull Message message) {
        messageChannel.accept(new MessageVisitor(message));
    }

    @Override
    public void send(MessageChannel messageChannel, byte[] bytes, String qualifiedName) {
        messageChannel.accept(new FileVisitor(bytes, qualifiedName));
    }

    @Override
    public User getSelfUser() {
        return UserFactory.create(Rank.USER, jda.getSelfUser().getIdLong(), jda.getSelfUser().getName());
    }

    @Nonnull
    private net.dv8tion.jda.api.entities.Message buildMessage(@Nonnull Message message){
        MessageBuilder messageBuilder = new MessageBuilder(message.getContent().orElse(""));

        buildMessageEmbed(message).ifPresent(messageBuilder::setEmbed);

        return messageBuilder.build();
    }

    @Nonnull
    private Optional<net.dv8tion.jda.api.entities.MessageEmbed> buildMessageEmbed(@Nonnull Message message){
        return message.getMessageEmbed().map(messageEmbed -> {
            EmbedBuilder embedBuilder = new EmbedBuilder();

            messageEmbed.getTitle().ifPresent(embedBuilder::setTitle);
            messageEmbed.getContent().ifPresent(embedBuilder::setDescription);
            messageEmbed.getTimestamp().ifPresent(embedBuilder::setTimestamp);
            messageEmbed.getThumbnail().ifPresent(embedBuilder::setThumbnail);
            messageEmbed.getFields().forEach(field ->
                    embedBuilder.addField(field.getTitle(), field.getContent().toString(), true)
            );

            return embedBuilder.build();
        });
    }

    private class FileVisitor implements ArchitectureVisitor {
        private final byte[] bytes;
        private final String qualifiedName;

        private FileVisitor(byte[] bytes, String qualifiedName){
            this.bytes = bytes;
            this.qualifiedName = qualifiedName;
        }

        @Override
        public void visit(@Nonnull TextChannel textChannel){
            net.dv8tion.jda.api.entities.TextChannel jdaTextChannel;

            jdaTextChannel = Preconditions.checkNotNull(jda.getTextChannelById(textChannel.getId()));
            jdaTextChannel.sendFile(bytes, qualifiedName).queue();
        }

        @Override
        public void visit(@Nonnull PrivateChannel privateChannel){
            net.dv8tion.jda.api.entities.PrivateChannel jdaPrivateChannel;

            jdaPrivateChannel = Preconditions.checkNotNull(jda.getPrivateChannelById(privateChannel.getId()));
            jdaPrivateChannel.sendFile(bytes, qualifiedName).queue();
        }
    }

    private class MessageVisitor implements ArchitectureVisitor {
        private final net.dv8tion.jda.api.entities.Message jdaMessage;
        private MessageVisitor(Message message){
            this.jdaMessage = buildMessage(message);
        }

        @Override
        public void visit(@Nonnull TextChannel textChannel){
            net.dv8tion.jda.api.entities.TextChannel jdaTextChannel;

            jdaTextChannel = Preconditions.checkNotNull(jda.getTextChannelById(textChannel.getId()));
            jdaTextChannel.sendMessage(jdaMessage).queue();
        }

        @Override
        public void visit(@Nonnull PrivateChannel privateChannel){
            net.dv8tion.jda.api.entities.PrivateChannel jdaPrivateChannel;

            jdaPrivateChannel = Preconditions.checkNotNull(jda.getPrivateChannelById(privateChannel.getId()));
            jdaPrivateChannel.sendMessage(jdaMessage).queue();
        }
    }
}
