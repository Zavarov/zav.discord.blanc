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

package zav.discord.blanc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.mock.LinkMock;
import zav.discord.blanc.mock.SubredditMock;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class MessageChannelTest extends AbstractTest{
    /*
    Subreddit subreddit;
    Submission submission;
    Submission nsfw;
    Submission spoiler;
    MessageEmbed messageEmbed;

    @BeforeEach
    public void setUp(){
        subreddit = SubredditFactory.create(
                SubredditMock::new,
                "Subreddit",
                "Description",
                54321,
                "id",
                Instant.now()
        );

        submission = SubmissionFactory.create(
                LinkMock::new,
                "Author",
                "Submission",
                12345,
                false,
                false,
                "id",
                Instant.now()
        );

        nsfw = SubmissionFactory.create(
                LinkMock::new,
                "Author",
                "Submission",
                12345,
                true,
                false,
                "id",
                Instant.now()
        );

        spoiler = SubmissionFactory.create(
                LinkMock::new,
                "Author",
                "Submission",
                12345,
                false,
                true,
                "id",
                Instant.now()
        );
        messageEmbed = new MessageEmbed();
    }

    @Test
    public void testSendSubmission(){
        textChannel.send(subreddit, submission);

        Message message = getSendMessage();
        assertThat(message.getContent().orElseThrow()).contains(subreddit.getName());
    }

    @Test
    public void testSendNsfwSubmission(){
        textChannel.send(subreddit, nsfw);

        Message message = getSendMessage();
        assertThat(message.getContent().orElseThrow()).contains(subreddit.getName());
        MessageEmbed messageEmbed = message.getMessageEmbeds(0);
        assertThat(messageEmbed.getColor()).contains(Color.RED);
    }

    @Test
    public void testSendSpoilerSubmission(){
        textChannel.send(subreddit, spoiler);

        Message message = getSendMessage();
        assertThat(message.getContent().orElseThrow()).contains(subreddit.getName());
        MessageEmbed messageEmbed = message.getMessageEmbeds(0);
        assertThat(messageEmbed.getColor()).contains(Color.BLACK);
    }

    @Test
    public void testSendMessageEmbed(){
        textChannel.send(messageEmbed);

        Message message = getSendMessage();
        MessageEmbed messageEmbed = message.getMessageEmbeds(0);
        assertThat(message.containsMessageEmbeds(messageEmbed)).isTrue();
    }

    @Test
    public void testSendGuild(){
        textChannel.send(guild);

        Message message = getSendMessage();
        MessageEmbed messageEmbed = message.getMessageEmbeds(0);
        assertThat(messageEmbed.getTitle()).map(Title::getName).contains("Guild");
        assertThat(messageEmbed.getContent()).contains(Long.toUnsignedString(guild.getId()));
    }

    @Test
    public void testSendRole(){
        textChannel.send(role);

        Message message = getSendMessage();
        MessageEmbed messageEmbed = message.getMessageEmbeds(0);
        assertThat(messageEmbed.getTitle()).map(Title::getName).contains("Role");
        assertThat(messageEmbed.getContent()).contains(Long.toUnsignedString(role.getId()));
    }

    @Test
    public void testSendMember(){
        textChannel.send(member);

        Message message = getSendMessage();
        MessageEmbed messageEmbed = message.getMessageEmbeds(0);
        assertThat(messageEmbed.getTitle()).map(Title::getName).contains("Member");
        assertThat(messageEmbed.getContent()).contains(Long.toUnsignedString(member.getId()));
    }

    @Test
    public void testSendUser(){
        textChannel.send(user);

        Message message = getSendMessage();
        MessageEmbed messageEmbed = message.getMessageEmbeds(0);
        assertThat(messageEmbed.getTitle()).map(Title::getName).contains("User");
        assertThat(messageEmbed.getContent()).contains(Long.toUnsignedString(user.getId()));
    }

    @Test
    public void testSendObject(){
        Object data = new Object();
        textChannel.send(data);

        Message message = getSendMessage();
        assertThat(message.getContent()).contains(data.toString());
    }

    @Test
    public void testSendImage() throws IOException {
        BufferedImage image = new BufferedImage(1024, 768, BufferedImage.TYPE_3BYTE_BGR);
        textChannel.send(image, "image.png");

        Message message = getSendMessage();
        assertThat(message.getAttachments()).hasSize(1);

        ByteArrayOutputStream imageData = new ByteArrayOutputStream();
        ImageIO.write(image, "png", imageData);
        byte[] expected = imageData.toByteArray();
        byte[] retrieved = message.getAttachments(0).retrieveContent().readAllBytes();

        assertThat(expected).containsExactly(retrieved);
    }

    @Test
    public void testSendString(){
        textChannel.send("%d + %d = %d", 1, 2, 3);
        Message message = getSendMessage();

        assertThat(message.getContent()).contains("1 + 2 = 3");
    }

    private Message getSendMessage(){
        assertThat(textChannel.messages).hasSize(1);
        return textChannel.messages.values().iterator().next();
    }
    */
}
