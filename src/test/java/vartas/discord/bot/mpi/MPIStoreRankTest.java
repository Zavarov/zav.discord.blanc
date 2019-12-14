package vartas.discord.bot.mpi;

import mpi.MPI;
import mpi.MPIException;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Before;
import org.junit.Test;
import vartas.discord.bot.AbstractTest;
import vartas.discord.bot.mpi.command.MPIStoreRank;
import vartas.discord.bot.mpi.serializable.MPIVoid;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

public class MPIStoreRankTest extends AbstractTest {
    MPIObserver observer;
    MPIStoreRank command;
    MPIVoid object;
    @Before
    public void setUp(){
        command = new MPIStoreRank();
        object = new MPIVoid();
        observer = new MPIObserver(shard);
    }

    @Test
    public void sendTest(){
        shard.send(MPIAdapter.MPI_MASTER_NODE, command, object);
        assertThat(shard.stored).hasSize(1);
    }

    @Test
    public void sendViaObserverTest() throws MPIException {
        byte[] bytes = SerializationUtils.serialize(object);
        ByteBuffer buffer = MPI.newByteBuffer(bytes.length);
        buffer.put(bytes);
        MPI.COMM_WORLD.iSend(buffer, bytes.length, MPI.BYTE, MPIAdapter.MPI_MASTER_NODE, MPICoreCommands.MPI_STORE_RANK.getCode());

        observer.run();
        assertThat(shard.stored).hasSize(1);
    }
}
