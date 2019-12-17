package vartas.discord.bot;

import org.junit.Before;
import org.junit.Test;
import vartas.discord.bot.entities.Status;

import static org.assertj.core.api.Assertions.assertThat;

public class StatusTrackerTest extends AbstractTest{
    StatusTracker tracker;

    @Before
    public void setUp(){
        tracker = new StatusTracker(shard, status);
        status.add("element");
    }

    @Test
    public void runTest(){
        assertThat(shard.send).hasSize(0);
        tracker.run();
        assertThat(shard.send).hasSize(1);
    }

    @Test
    public void runWithoutStatusTest(){
        tracker = new StatusTracker(shard, new Status());

        tracker.run();
        assertThat(shard.send).isEmpty();
    }
}
