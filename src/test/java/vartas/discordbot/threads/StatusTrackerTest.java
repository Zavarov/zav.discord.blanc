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

import net.dv8tion.jda.core.entities.Game;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import vartas.discordbot.comm.Environment;
import vartas.discordbot.comm.OfflineEnvironment;

/**
 *
 * @author u/Zavarov
 */
public class StatusTrackerTest{
    static Environment environment;
    @BeforeClass
    public static void create(){
        environment = new OfflineEnvironment();
    }
    StatusTracker tracker;
    @Before
    public void setUp() {
        tracker = new StatusTracker(environment);
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
        
        assertNull(environment.comm(0).presence().getGame());
        tracker.run();
        assertEquals(environment.comm(0).presence().getGame(),Game.playing("statusmessage"));
    }
}
