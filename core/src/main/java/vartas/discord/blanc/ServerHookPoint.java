package vartas.discord.blanc;

import vartas.discord.blanc.factory.FieldFactory;
import vartas.discord.blanc.factory.MessageEmbedFactory;
import vartas.discord.blanc.factory.MessageFactory;
import vartas.reddit.Submission;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.Optional;

/**
 * This interface provides the hook point for passing messages to the Discord server.
 */
public interface ServerHookPoint extends ServerHookPointTOP{
    /**
     * Wraps the {@link Submission} around a {@link Message} and submits them to the Discord server.
     * @param messageChannel the {@link MessageChannel} the message is submitted to.
     * @param submission the content of the submitted {@link Message}
     */
    @Override
    default void send(@Nonnull MessageChannel messageChannel, @Nonnull Submission submission) {
        MessageEmbed messageEmbed = MessageEmbedFactory.create();

        messageEmbed.setTitle(Optional.of(submission.getTitle()));
        messageEmbed.setThumbnail(submission.getThumbnail());
        messageEmbed.setTimestamp(Optional.of(submission.getCreated()));

        send(messageChannel, messageEmbed);
    }

    /**
     * Wraps the {@link MessageEmbed} around a {@link Message} and submits them to the Discord server.
     * @param messageChannel the {@link MessageChannel} the message is submitted to.
     * @param messageEmbed the content of the submitted {@link Message}
     */
    @Override
    default void send(@Nonnull MessageChannel messageChannel, @Nonnull MessageEmbed messageEmbed) {
        Message message = MessageFactory.create(0, Instant.now(), null);

        message.setMessageEmbed(Optional.of(messageEmbed));

        send(messageChannel, message);
    }

    @Override
    default void send(@Nonnull MessageChannel messageChannel, @Nonnull Guild guild) {
        MessageEmbed messageEmbed = MessageEmbedFactory.create();

        messageEmbed.setTitle(guild.getName());
        messageEmbed.addFields(FieldFactory.create("#TextChannels", guild.getChannels().size()));
        messageEmbed.addFields(FieldFactory.create("#Roles", guild.getRoles().size()));
        messageEmbed.addFields(FieldFactory.create("#Members", guild.getRoles().size()));

        send(messageChannel, messageEmbed);
    }

    @Override
    default void send(@Nonnull MessageChannel messageChannel, @Nonnull Role role) {
        MessageEmbed messageEmbed = MessageEmbedFactory.create();

        messageEmbed.setTitle(role.getName());
        messageEmbed.addFields(FieldFactory.create("id", role.getId()));
        role.getGroup().ifPresent(group -> messageEmbed.addFields(FieldFactory.create("group", group)));

        send(messageChannel, messageEmbed);
    }

    @Override
    default void send(@Nonnull MessageChannel messageChannel, @Nonnull Member member) {
        Message message = MessageFactory.create(0, Instant.now(), null);

        message.setContent(member.toString());

        send(messageChannel, message);
    }

    @Override
    default void send(@Nonnull MessageChannel messageChannel, @Nonnull Object object) {
        Message message = MessageFactory.create(0, Instant.now(), null);

        message.setContent(object.toString());

        send(messageChannel, message);
    }
}
