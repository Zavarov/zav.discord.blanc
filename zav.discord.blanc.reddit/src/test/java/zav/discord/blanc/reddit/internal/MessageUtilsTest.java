/*
 * Copyright (c) 2022 Zavarov.
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

package zav.discord.blanc.reddit.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Color;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.jrc.databind.LinkEntity;

/**
 * Checks whether the message created from a Reddit link contains the expected fields.
 */
public class MessageUtilsTest {
  
  LinkEntity link;
  
  /**
   * Create a dummy Link DTO.
   */
  @BeforeEach
  public void setUp() {
    link = new LinkEntity();
    link.setOver18(true);
    link.setSpoiler(true);
    link.setAuthor("author");
    link.setThumbnail("https://www.foo.bar/image.jpg");
    link.setLinkFlairText("flair");
  }
  
  @Test
  public void testEmbedColor() {
    // Over18 -> true && Spoiler -> true
    MessageEmbed embed = getMessageEmbed();
    assertThat(embed.getColor()).isEqualTo(Color.RED);
    
    // Over18 -> false && Spoiler -> true
    link.setOver18(false);
    embed = getMessageEmbed();
    assertThat(embed.getColor()).isEqualTo(Color.BLACK);
  
    // Over18 -> false && Spoiler -> false
    link.setSpoiler(false);
    embed = getMessageEmbed();
    assertThat(embed.getColor()).isNotEqualTo(Color.RED);
    assertThat(embed.getColor()).isNotEqualTo(Color.BLACK);
  }
  
  @Test
  public void testTimeStamp() {
    MessageEmbed embed = getMessageEmbed();
    assertThat(embed.getTimestamp()).isNull();
    
    link.setCreatedUtc(0.0);
    embed = getMessageEmbed();
    assertThat(embed.getTimestamp()).isNotNull();
  }
  
  @Test
  public void testThumbnail() {
    // Over18 -> true && Spoiler -> true
    MessageEmbed embed = getMessageEmbed();
    assertThat(embed.getThumbnail()).isNull();
  
    // Over18 -> false && Spoiler -> true
    link.setOver18(false);
    embed = getMessageEmbed();
    assertThat(embed.getThumbnail()).isNull();
  
    // Over18 -> false && Spoiler -> false
    link.setSpoiler(false);
    embed = getMessageEmbed();
    assertThat(embed.getThumbnail()).isNotNull();
    
    link.setThumbnail(null);
    embed = getMessageEmbed();
    assertThat(embed.getThumbnail()).isNull();
  
    link.setThumbnail("xxx");
    embed = getMessageEmbed();
    assertThat(embed.getThumbnail()).isNull();
  }
  
  private MessageEmbed getMessageEmbed() {
    return MessageUtils.forLink(link).getEmbeds().get(0);
  }
}
