package vartas.discord.bot.mpi.command;

import com.google.common.base.Preconditions;
import mpi.*;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.visitor.ShardVisitor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * This class provides the body of all MPI commands.
 * @param <T> the type of message associated with the command
 */
public abstract class MPICommand <T extends Serializable> implements ShardVisitor {
    private final Logger log = JDALogger.getLog(this.getClass());
    /**
     * The received message associated with the command.
     */
    @Nullable
    protected T message;
    /**
     * Each command is reduced to a byte-stream when sending them over MPI. In order to distinguish between those,
     * we need an unique identifier for each command.
     * @return the command id
     */
    protected abstract short getCode();

    /**
     * Unless specified otherwise, the tag should be equivalent to {@link #getCode}.
     * @return the MPI tag of the command
     */
    protected short getTag(){
        return getCode();
    }

    /**
     * This sub-class handles all messages the command sends.
     */
    public abstract class MPISendCommand{
        /**
         * Sends the specified object over the MPI channel.<br>
         * This transmission can be done via a simple {@link Intercomm#send(Object, int, Datatype, int, int)} in case of
         * a point-to-point communication, a {@link Intercomm#bcast(Object, int, Datatype, int)} for collective
         * communication or any other form.<br>
         * To avoid race conditions, it is recommended have the transmit commands be asynchronous, for example when the
         * recipient is waiting for a collective command, while we do a point-to-point command.<br>
         * @param object the specified object
         * @throws MPIException if an MPI exception occurred
         * @throws NullPointerException if {@code object} is null
         */
        public abstract void send(@Nonnull T object) throws MPIException, NullPointerException;
    }

    /**
     * This sub-class handles all messages the command receives.
     */
    public class MPIReceiveCommand {
        /**
         * Receives the incoming message and modifies the state of the shard accordingly.
         * This can be done via a simple {@link Intercomm#recv(Object, int, Datatype, int, int)} in case of a point-to-point
         * communication, a {@link Intercomm#gather(Object, int, Datatype, int)} for collective communication or any other
         * form.
         * @param shard the shard receiving this command
         * @throws MPIException if an MPI exception occurred
         * @throws NullPointerException if {@code shard} is null
         */
        public void accept(@Nonnull Shard shard) throws MPIException, NullPointerException{
            Preconditions.checkNotNull(shard);
            message = receive(getTag());
            handle(shard);
        }

        /**
         * Receives the message from an arbitrary node. Since the message was sent asynchronously, we can't know which
         * node the command was from.
         */
        @Nonnull
        public T receive(int tag) throws MPIException {
            Status status = MPI.COMM_WORLD.probe(MPI.ANY_SOURCE, tag);

            log.debug(String.format("Message received from %d in node %d", status.getSource(), MPI.COMM_WORLD.getRank()));

            int length = status.getElements(MPI.BYTE);
            byte[] bytes = new byte[length];
            MPI.COMM_WORLD.recv(bytes, length, MPI.BYTE, status.getSource(), status.getTag());
            return SerializationUtils.deserialize(bytes);
        }
    }
}
