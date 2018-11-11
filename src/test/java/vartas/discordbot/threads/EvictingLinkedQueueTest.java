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
package vartas.discordbot.threads;

import java.util.Iterator;
import static java.util.concurrent.TimeUnit.DAYS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.discordbot.threads.ActivityTracker.EvictingLinkedQueue;

/**
 *
 * @author u/Zavarov
 */
public class EvictingLinkedQueueTest {
    EvictingLinkedQueue<String> queue;
    @Before
    public void setUp(){
        queue = new EvictingLinkedQueue<>((int)DAYS.toMinutes(1)/2);
        queue.list.add("a");
        queue.list.add("b");
    }
    
    @Test
    public void iteratorTest(){
        Iterator<String> iterator = queue.iterator();
        assertEquals(iterator.next(),"a");
        assertEquals(iterator.next(),"b");
        assertFalse(iterator.hasNext());
    }
    
    @Test
    public void sizeTest(){
        assertEquals(queue.size(),2);
    }
    
    @Test
    public void offerTest(){
        assertTrue(queue.offer("c"));
        assertFalse(queue.contains("a"));
        assertEquals(queue.size(),2);
    }
    
    @Test
    public void pollTest(){
        assertEquals(queue.poll(),"a");
        assertEquals(queue.poll(),"b");
        assertNull(queue.poll());
    }
    
    @Test
    public void peekTest(){
        assertEquals(queue.peek(),"a");
    }
    
    @Test
    public void tailTest(){
        assertEquals(queue.tail(),"b");
    }
    
    @Test
    public void peekEmptyTest(){
        queue.clear();
        assertNull(queue.peek());
    }
    
    @Test
    public void tailEmptyTest(){
        queue.clear();
        assertNull(queue.tail());
    }
}