package vartas.discord.bot;

import mpi.MPI;
import mpi.MPIException;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static final Path credentials = Paths.get("src/test/resources/credentials.json");
    public static final Path status = Paths.get("src/test/resources/status.json");
    public static final Path rank = Paths.get("src/test/resources/rank.json");
    public static final Path guilds = Paths.get("src/test/resources/guilds");

    public static void main(String[] args) throws MPIException {
        int test = MPI.InitThread(args, MPI.THREAD_SERIALIZED);

        System.out.println(test);

        int myRank = MPI.COMM_WORLD.getRank();

        System.out.println("Hello World from rank "+myRank);

        MPI.Finalize();
    }
}
