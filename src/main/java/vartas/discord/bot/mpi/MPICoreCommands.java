package vartas.discord.bot.mpi;

public enum MPICoreCommands implements MPIStatusCode{
    MPI_ADD_REDDIT_FEED_TO_TEXT_CHANNEL((short)0),
    MPI_REMOVE_REDDIT_FEED_FROM_TEXT_CHANNEL((short)1),
    MPU_UPDATE_RANK((short)10),
    MPI_UPDATE_STATUS_MESSAGE((short)20),
    MPI_SEND_SUBMISSION((short)30),
    MPI_SHUTDOWN((short)40);

    private final short code;

    MPICoreCommands(short code){
        this.code = code;
    }

    @Override
    public short getCode() {
        return code;
    }
}
