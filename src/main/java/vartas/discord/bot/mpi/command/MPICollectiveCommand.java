package vartas.discord.bot.mpi.command;

import mpi.Datatype;
import mpi.Intercomm;
import mpi.MPI;
import mpi.MPIException;
import org.apache.commons.lang3.SerializationUtils;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public abstract class MPICollectiveCommand <T extends Serializable> extends MPICommand<T>{
    public class MPICollectiveSendCommand extends MPISendCommand{
        /**
         * Transmits the specified object all nodes.<br>
         * This method effectively executes {@link Intercomm#iBcast(Buffer, int, Datatype, int)}.
         * However, since we can't use {@link Intercomm#iProbe(int, int)} on collective commands, we have to
         * instead use multiple asynchronous send commands.
         * @throws IllegalArgumentException if no target node has been specified
         */
        @Override
        public void send(@Nonnull T object) throws MPIException, NullPointerException {
            byte[] bytes = SerializationUtils.serialize(object);
            ByteBuffer buffer = MPI.newByteBuffer(bytes.length);
            buffer.put(bytes);
            for(int i = 0 ; i < MPI.COMM_WORLD.getSize() ; ++i)
                MPI.COMM_WORLD.iSend(buffer, bytes.length, MPI.BYTE, i, getTag());
        }
    }
}
