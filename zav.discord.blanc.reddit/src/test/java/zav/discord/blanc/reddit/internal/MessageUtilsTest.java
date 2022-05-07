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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
    assertEquals(embed.getColor(), Color.RED);
    
    // Over18 -> false && Spoiler -> true
    link.setOver18(false);
    embed = getMessageEmbed();
    assertEquals(embed.getColor(), Color.BLACK);
  
    // Over18 -> false && Spoiler -> false
    link.setSpoiler(false);
    embed = getMessageEmbed();
    assertNotEquals(embed.getColor(), Color.RED);
    assertNotEquals(embed.getColor(), Color.BLACK);
  }
  
  @Test
  public void testTimeStamp() {
    MessageEmbed embed = getMessageEmbed();
    assertNull(embed.getTimestamp());
    
    link.setCreatedUtc(0.0);
    embed = getMessageEmbed();
    assertNotNull(embed.getTimestamp());
  }
  
  @Test
  public void testThumbnail() {
    // Over18 -> true && Spoiler -> true
    MessageEmbed embed = getMessageEmbed();
    assertNull(embed.getThumbnail());
  
    // Over18 -> false && Spoiler -> true
    link.setOver18(false);
    embed = getMessageEmbed();
    assertNull(embed.getThumbnail());
  
    // Over18 -> false && Spoiler -> false
    link.setSpoiler(false);
    embed = getMessageEmbed();
    assertNotNull(embed.getThumbnail());
    
    link.setThumbnail(null);
    embed = getMessageEmbed();
    assertNull(embed.getThumbnail());
  
    link.setThumbnail("xxx");
    embed = getMessageEmbed();
    assertNull(embed.getThumbnail());
  }
  
  private MessageEmbed getMessageEmbed() {
    return MessageUtils.forLink(link).getEmbeds().get(0);
  }
}
