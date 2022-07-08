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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.Color;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.StringUtils;
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
    link.setUrl("https://www.foo.bar");
  }
  
  @Test
  public void testPermalink() {
    MessageEmbed embed = getMessageEmbed();
    assertNotNull(embed.getUrl());
  }
  
  @Test
  public void testPermalinkTooLong() {
    // Too long -> null
    link.setPermalink(StringUtils.repeat("x", MessageEmbed.URL_MAX_LENGTH));
    MessageEmbed embed = getMessageEmbed();
    assertNull(embed.getUrl());
  }
  
  @Test
  public void testUrl() {
    MessageEmbed embed = getMessageEmbed();
    assertNotNull(embed.getAuthor());
    assertNotNull(embed.getAuthor().getUrl());
  }
  
  @Test
  public void testUrlTooLong() {
    // Too long -> null
    link.setUrl(StringUtils.repeat("x", MessageEmbed.URL_MAX_LENGTH));
    MessageEmbed embed = getMessageEmbed();
    assertNotNull(embed.getAuthor());
    assertNull(embed.getAuthor().getUrl());
  }
  
  @Test
  public void testEmbedColorOver18() {
    // Over18 -> true && Spoiler -> true
    MessageEmbed embed = getMessageEmbed();
    assertEquals(embed.getColor(), Color.RED);
  }
  
  @Test
  public void testEmbedColorSpoiler() {
    // Over18 -> false && Spoiler -> true
    link.setOver18(false);
    MessageEmbed embed = getMessageEmbed();
    assertEquals(embed.getColor(), Color.BLACK);
  }
  
  @Test
  public void testEmbedColor() {
    // Over18 -> false && Spoiler -> false
    link.setOver18(false);
    link.setSpoiler(false);
    MessageEmbed embed = getMessageEmbed();
    assertNotEquals(embed.getColor(), Color.RED);
    assertNotEquals(embed.getColor(), Color.BLACK);
  }
  
  @Test
  public void testMissingTimeStamp() {
    MessageEmbed embed = getMessageEmbed();
    assertNull(embed.getTimestamp());
  }
  
  @Test
  public void testTimeStamp() {
    link.setCreatedUtc(0.0);
    MessageEmbed embed = getMessageEmbed();
    assertNotNull(embed.getTimestamp());
  }
  
  @Test
  public void testThumbnailOver18AndSpoiler() {
    // Over18 -> true && Spoiler -> true
    MessageEmbed embed = getMessageEmbed();
    assertNull(embed.getThumbnail());
  }
  
  @Test
  public void testThumbnailSpoiler() {
    // Over18 -> false && Spoiler -> true
    link.setOver18(false);
    MessageEmbed embed = getMessageEmbed();
    assertNull(embed.getThumbnail());
  }
  
  @Test
  public void testThumbnail() {
    // Over18 -> false && Spoiler -> false
    link.setOver18(false);
    link.setSpoiler(false);
    MessageEmbed embed = getMessageEmbed();
    assertNotNull(embed.getThumbnail());
  }
  
  @Test
  public void testMissingThumbnail() {
    link.setOver18(false);
    link.setSpoiler(false);
    link.setThumbnail(null);
    MessageEmbed embed = getMessageEmbed();
    assertNull(embed.getThumbnail());
  }
  
  @Test
  public void testThumbnailTooLong() {
    link.setOver18(false);
    link.setSpoiler(false);
    // Too long -> null
    link.setThumbnail("https://www.foo.bar/" + StringUtils.repeat("x", MessageEmbed.URL_MAX_LENGTH));
    MessageEmbed embed = getMessageEmbed();
    assertNull(embed.getThumbnail());
  }
  
  @Test
  public void testInvalidThumbnail() {
    // Invalid URL -> null
    link.setOver18(false);
    link.setSpoiler(false);
    link.setThumbnail("xxx");
    MessageEmbed embed = getMessageEmbed();
    assertNull(embed.getThumbnail());
  }
  
  @Test
  public void testLinkFlairTest() {
    MessageEmbed embed = getMessageEmbed();
    assertThat(embed.getTitle()).startsWith("[flair]");
  }
  
  @Test
  public void testLinkFlairTestWithBrackets() {
    // Brackets should be omitted when the already exist
    link.setLinkFlairText("[flair]");
    MessageEmbed embed = getMessageEmbed();
    assertThat(embed.getTitle()).startsWith("[flair]");
  }
  
  private MessageEmbed getMessageEmbed() {
    return MessageUtils.forLink(link).getEmbeds().get(0);
  }
}
