package vartas.discord.bot.internal;

import org.junit.Before;
import org.junit.Test;
import vartas.discord.bot.AbstractTest;
import vartas.discord.bot.entities.offline.OfflineCluster;
import vartas.discord.bot.entities.offline.OfflineShard;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateStatusMessageTest extends AbstractTest{
    OfflineCluster cluster;
    OfflineShard shard;
    UpdateStatusMessage command;
    @Before
    public void setUp(){
        cluster = OfflineCluster.create();
        shard = OfflineShard.create(cluster);
        cluster.registerShard(shard);
        command = new UpdateStatusMessage();
    }

    @Test
    public void visitorTest(){
        cluster.accept(command);
        assertThat(shard.send).hasSize(1);
    }
}
