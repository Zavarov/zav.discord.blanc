package vartas.discord.bot.mpi.command;

import com.google.common.base.Preconditions;
import mpi.MPI;
import mpi.MPIException;
import mpi.Status;
import org.apache.commons.lang3.SerializationUtils;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.visitor.ShardVisitor;

import java.io.Serializable;

public abstract class MPICommand <T extends Serializable> implements ShardVisitor {
    protected T message;
    protected int myRank;
    protected int numProcs;

    protected abstract int getCode();

    public void accept(Shard shard) throws MPIException{
        Preconditions.checkNotNull(shard);
        T message = receive();
        accept(shard, message);
    }

    public void accept(Shard shard, T message){
        Preconditions.checkNotNull(shard);
        Preconditions.checkNotNull(message);
        this.message = message;
        handle(shard);
    }

    public T receive() throws MPIException{
        Status status = MPI.COMM_WORLD.probe(MPI.ANY_SOURCE, getCode());
        int length = status.getElements(MPI.BYTE);
        byte[] bytes = new byte[length];
        MPI.COMM_WORLD.recv(bytes, length, MPI.BYTE, status.getSource(), status.getTag());
        return SerializationUtils.deserialize(bytes);
    }

    public void send(int shardId, T object) throws MPIException{
        byte[] bytes = SerializationUtils.serialize(object);
        MPI.COMM_WORLD.send(bytes, bytes.length, MPI.BYTE, shardId, getCode());
    }
}
