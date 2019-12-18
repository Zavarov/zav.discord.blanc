package vartas.discord.bot.mpi.command;

import mpi.MPI;
import mpi.MPIException;
import org.apache.commons.lang3.SerializationUtils;
import vartas.discord.bot.mpi.MPIAdapter;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.nio.ByteBuffer;

public abstract class MPIPointToPointCommand <T extends Serializable> extends MPICommand<T>{
    public class MPIPointToPointSendCommand extends MPISendCommand{
        private final int targetNode;

        public MPIPointToPointSendCommand(){
            this(MPIAdapter.MPI_MASTER_NODE);
        }

        public MPIPointToPointSendCommand(int targetNode){
            this.targetNode = targetNode;
        }

        /**
         * Transmits the specified object to the target node, specified in {@link #targetNode}.
         * @throws IllegalArgumentException if no target node has been specified
         */
        @Override
        public void send(@Nonnull T object) throws MPIException, NullPointerException {
            byte[] bytes = SerializationUtils.serialize(object);
            ByteBuffer buffer = MPI.newByteBuffer(bytes.length);
            buffer.put(bytes);
            MPI.COMM_WORLD.iSend(buffer, bytes.length, MPI.BYTE, targetNode, getTag());
        }
    }
}