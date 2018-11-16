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
import org.apache.commons.lang3.StringUtils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * @author u/Zavarov
 */
public class LogListenerTest {
    LogListener<String> listener;
    @Before
    public void setUp(){
        listener = new LogListener<>();
        LogListener.MEMORY.clear();
    }
    @Test
    public void appendTest(){
        assertTrue(LogListener.MEMORY.isEmpty());
        listener.append("text1");
        listener.append("text2");
        listener.append("text3");
        Iterator<Object> iterator = LogListener.MEMORY.iterator();
        assertEquals(iterator.next(),"text1");
        assertEquals(iterator.next(),"text2");
        assertEquals(iterator.next(),"text3");
        assertFalse(iterator.hasNext());
    }
    @Test
    public void appendTooLongTest(){
        String s1 = StringUtils.repeat('a', 101);
        String s2 = StringUtils.repeat('a', 97) + "...";
        
        assertTrue(LogListener.MEMORY.isEmpty());
        listener.append(s1);
        Iterator<Object> iterator = LogListener.MEMORY.iterator();
        assertEquals(iterator.next(),s2);
        assertFalse(iterator.hasNext());
    }
}