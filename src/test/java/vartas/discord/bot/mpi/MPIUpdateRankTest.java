package vartas.discord.bot.mpi;

import mpi.MPI;
import mpi.MPIException;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Before;
import org.junit.Test;
import vartas.discord.bot.AbstractTest;
import vartas.discord.bot.mpi.command.MPIUpdateRank;
import vartas.discord.bot.mpi.serializable.MPIRank;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

public class MPIUpdateRankTest extends AbstractTest {
    MPIObserver observer;
    MPIUpdateRank.MPISendCommand command;
    MPIRank object;
    @Before
    public void setUp(){
        command = MPIUpdateRank.createSendCommand();
        object = new MPIRank(rank);
        observer = new MPIObserver(shard);
    }

    @Test
    public void sendTest(){
        shard.send(command, object);
        assertThat(shard.stored).hasSize(1);
    }

    @Test
    public void sendViaObserverTest() throws MPIException {
        byte[] bytes = SerializationUtils.serialize(object);
        ByteBuffer buffer = MPI.newByteBuffer(bytes.length);
        buffer.put(bytes);
        MPI.COMM_WORLD.iSend(buffer, bytes.length, MPI.BYTE, MPIAdapter.MPI_MASTER_NODE, MPICoreCommands.MPU_UPDATE_RANK.getCode());

        observer.run();
        assertThat(shard.stored).hasSize(1);
    }
}
