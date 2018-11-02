/*
 * Copyright (C) 2017 u/Zavarov
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

package vartas.discordbot;

import java.util.Iterator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * @author u/Zavarov
 */
public class DiscordLogListenerTest {
    DiscordLogListener<String> listener;
    @Before
    public void setUp(){
        listener = new DiscordLogListener<>();
        DiscordLogListener.MEMORY.clear();
    }
    @Test
    public void appendTest(){
        assertTrue(DiscordLogListener.MEMORY.isEmpty());
        listener.append("text1");
        listener.append("text2");
        listener.append("text3");
        Iterator<Object> iterator = DiscordLogListener.MEMORY.iterator();
        assertEquals(iterator.next(),"text1");
        assertEquals(iterator.next(),"text2");
        assertEquals(iterator.next(),"text3");
        assertFalse(iterator.hasNext());
    }
}
