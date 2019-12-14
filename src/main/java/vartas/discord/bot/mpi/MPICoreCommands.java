package vartas.discord.bot.mpi;

public enum MPICoreCommands implements MPIStatusCode{
    MPI_ADD_REDDIT_FEED_TO_TEXT_CHANNEL(0),
    MPI_REMOVE_REDDIT_FEED_FROM_TEXT_CHANNEL(1),
    MPI_ADD_RANK(10),
    MPI_REMOVE_RANK(11),
    MPI_STORE_RANK(12),
    MPI_UPDATE_STATUS_MESSAGE(20),
    MPI_SEND_SUBMISSION(30),
    MPI_SHUTDOWN(40);

    private final int code;

    MPICoreCommands(int code){
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }
}
