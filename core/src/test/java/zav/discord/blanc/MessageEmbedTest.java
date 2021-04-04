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

import static org.assertj.core.api.Assertions.assertThat;

public class MessageEmbedTest extends AbstractTest{
    MessageEmbed messageEmbed;
    @BeforeEach
    public void setUp(){
        messageEmbed = new MessageEmbed();
    }

    @Test
    public void testAddFields(){
        messageEmbed.addFields("Title", "Content");
        assertThat(messageEmbed.sizeFields()).isEqualTo(1);
        assertThat(messageEmbed.getFields(0).getTitle()).isEqualTo("Title");
        assertThat(messageEmbed.getFields(0).getContent()).isEqualTo("Content");
        assertThat(messageEmbed.getFields(0).getInline()).isEqualTo(false);
    }

    @Test
    public void testSetTitle(){
        messageEmbed.setTitle("Title");
        assertThat(messageEmbed.getTitle()).map(Title::getName).contains("Title");
        assertThat(messageEmbed.getTitle()).flatMap(Title::getUrl).isEmpty();

        messageEmbed.setTitle("Title", "URL");
        assertThat(messageEmbed.getTitle()).map(Title::getName).contains("Title");
        assertThat(messageEmbed.getTitle()).flatMap(Title::getUrl).contains("URL");
    }

    @Test
    public void testSetAuthor(){
        messageEmbed.setAuthor("Author");
        assertThat(messageEmbed.getAuthor()).map(Author::getName).contains("Author");
        assertThat(messageEmbed.getAuthor()).flatMap(Author::getUrl).isEmpty();

        messageEmbed.setAuthor("Author", "URL");
        assertThat(messageEmbed.getAuthor()).map(Author::getName).contains("Author");
        assertThat(messageEmbed.getAuthor()).flatMap(Author::getUrl).contains("URL");
    }

    @Test
    public void testGetRealThis(){
        assertThat(messageEmbed.getRealThis()).isEqualTo(messageEmbed);
    }
}
