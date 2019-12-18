package vartas.discord.bot.mpi;

import mpi.MPI;
import mpi.MPIException;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Before;
import org.junit.Test;
import vartas.discord.bot.AbstractTest;
import vartas.discord.bot.mpi.command.MPIUpdateStatusMessage;
import vartas.discord.bot.mpi.serializable.MPIStatusMessageModification;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

public class MPIUpdateStatusMessageTest extends AbstractTest {
    MPIObserver observer;
    MPIUpdateStatusMessage.MPISendCommand command;
    MPIStatusMessageModification object;
    @Before
    public void setUp(){
        status.add("element");
        command = MPIUpdateStatusMessage.createSendCommand();
        object = new MPIStatusMessageModification(status.get().orElseThrow());
        observer = new MPIObserver(shard);
    }

    @Test
    public void sendTest(){
        shard.send(command, object);
        assertThat(shard.send).hasSize(1);
    }

    @Test
    public void sendViaObserverTest() throws MPIException {
        byte[] bytes = SerializationUtils.serialize(object);
        ByteBuffer buffer = MPI.newByteBuffer(bytes.length);
        buffer.put(bytes);
        MPI.COMM_WORLD.iSend(buffer, bytes.length, MPI.BYTE, MPIAdapter.MPI_MASTER_NODE, MPICoreCommands.MPI_UPDATE_STATUS_MESSAGE.getCode());

        observer.run();
        assertThat(shard.send).hasSize(1);
    }
}
