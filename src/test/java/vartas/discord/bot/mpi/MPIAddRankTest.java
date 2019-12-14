package vartas.discord.bot.mpi;

import mpi.MPI;
import mpi.MPIException;
import net.dv8tion.jda.internal.entities.UserImpl;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vartas.discord.bot.AbstractTest;
import vartas.discord.bot.entities.Rank;
import vartas.discord.bot.mpi.command.MPIAddRank;
import vartas.discord.bot.mpi.serializable.MPIRankModification;

import java.nio.ByteBuffer;

public class MPIAddRankTest extends AbstractTest {
    MPIObserver observer;
    MPIAddRank command;
    MPIRankModification object;
    UserImpl user;
    @Before
    public void setUp(){
        user = new UserImpl(userId, null);
        command = new MPIAddRank();
        object = new MPIRankModification(userId, Rank.Ranks.DEVELOPER.ordinal());
        observer = new MPIObserver(shard);
    }

    @After
    public void tearDown(){
        addRank(user, Rank.Ranks.DEVELOPER);
    }

    @Test
    public void sendTest(){
        shard.send(MPIAdapter.MPI_MASTER_NODE, command, object);
        checkRank(user, Rank.Ranks.DEVELOPER, true);
    }

    @Test
    public void sendViaObserverTest() throws MPIException {
        byte[] bytes = SerializationUtils.serialize(object);
        ByteBuffer buffer = MPI.newByteBuffer(bytes.length);
        buffer.put(bytes);
        MPI.COMM_WORLD.iSend(buffer, bytes.length, MPI.BYTE, MPIAdapter.MPI_MASTER_NODE, MPICoreCommands.MPI_ADD_RANK.getCode());

        observer.run();
        checkRank(user, Rank.Ranks.DEVELOPER, true);
    }
}
