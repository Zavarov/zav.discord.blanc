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

import java.io.File;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.OfflineJDA;

/**
 *
 * @author u/Zavarov
 */
public class StatusTrackerTest{
    JDAImpl jda;
    StatusTracker tracker;
    @Before
    public void setUp() {
        jda = new OfflineJDA();
        tracker = new StatusTracker(jda, new File("src/test/resources/status.xml"),10);
    }
    @Test
    public void shutdownTest(){
        assertFalse(tracker.executor.isShutdown());
        tracker.shutdown();
        assertTrue(tracker.executor.isShutdown());
    }
    @Test
    public void runTest(){
        tracker.executor.shutdownNow();
        while(!tracker.executor.isTerminated()){}
        
        jda.getPresence().setGame(null);
        assertNull(jda.getPresence().getGame());
        tracker.run();
        assertEquals(jda.getPresence().getGame().getName(),"statusmessage");
    }
}
