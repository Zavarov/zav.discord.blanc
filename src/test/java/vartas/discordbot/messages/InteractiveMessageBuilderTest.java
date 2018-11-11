/*
 * Copyright (C) 2018 u/Zavarov
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
package vartas.discordbot.messages;

import java.util.Arrays;
import net.dv8tion.jda.core.EmbedBuilder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author u/Zavarov
 */
public class InteractiveMessageBuilderTest {
    InteractiveMessage.Builder builder;
    @Before
    public void setUp(){
        builder = new InteractiveMessage.Builder(null,null,null);
    }
    @Test
    public void addDescriptionTest(){
        assertNull(builder.description);
        builder.addDescription("description");
        assertEquals(builder.description,"description");
    }
    @Test
    public void addLineTest(){
        assertEquals(builder.current_page.length(),0);
        builder.addLine("line");
        assertEquals(builder.current_page.toString(),"line\n");
    }
    @Test
    public void addLinesTest(){
        assertEquals(builder.current_page.length(),0);
        builder.addLines(Arrays.asList("a","b","c"),2);
        assertEquals(builder.current_page.toString(),"c\n");
    }
    @Test
    public void nextPageTest(){
        builder.current_page.append("junk");
        assertTrue(builder.embeds.isEmpty());
        builder.nextPage();
        assertEquals(builder.embeds.size(),1);
        assertTrue(builder.embeds.get(0).getFields().get(0).getValue().contains("junk"));
        assertEquals(builder.current_page.length(),0);
    }
    @Test
    public void addPageTest(){
        EmbedBuilder embed = new EmbedBuilder();
        assertEquals(builder.embeds.size(),0);
        builder.addPage(embed);
        assertEquals(builder.embeds.size(),1);
    }
    @Test
    public void nextPageDescriptionTest(){
        builder.current_page.append("junk");
        builder.description = "desc";
        assertTrue(builder.embeds.isEmpty());
        builder.nextPage();
        assertEquals(builder.embeds.size(),1);
        assertTrue(builder.embeds.get(0).getFields().get(0).getValue().contains("desc"));
        assertTrue(builder.embeds.get(0).getFields().get(1).getValue().contains("junk"));
        assertEquals(builder.current_page.length(),0);
    }
    @Test
    public void buildTest(){
        builder.current_page.append("junk");
        InteractiveMessage message = builder.build();
        assertEquals(message.pages.size(),1);
        assertTrue(message.pages.get(0).getTitle().contains("Page 0/0"));
    }
    @Test
    public void setThumbnailTest(){
        assertNull(builder.thumbnail);
        builder.setThumbnail("url");
        assertEquals(builder.thumbnail,"url");
    }
    @Test
    public void buildThumbnailTest(){
        builder.current_page.append("junk");
        builder.setThumbnail("https://www.test.com");
        InteractiveMessage message = builder.build();
        assertEquals(message.pages.size(),1);
        assertEquals(message.pages.get(0).getThumbnail().getUrl(),"https://www.test.com");
    }
}