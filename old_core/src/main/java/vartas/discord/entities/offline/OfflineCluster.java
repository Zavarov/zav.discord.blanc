package vartas.discord.entities.offline;

import org.jetbrains.annotations.NotNull;
import vartas.discord.EntityAdapter;
import vartas.discord.JSONEntityAdapter;
import vartas.discord.entities.Cluster;
import vartas.discord.entities.Credentials;
import vartas.reddit.Client;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.nio.file.Paths;


public class OfflineCluster extends Cluster {
    public static final Path Credentials = Paths.get("src/test/resources/credentials.json");
    public static final Path Status = Paths.get("src/test/resources/status.json");
    public static final Path Rank = Paths.get("src/test/resources/rank.json");
    public static final Path Guilds = Paths.get("src/test/resources/guilds");
    public static final EntityAdapter Adapter = new JSONEntityAdapter(Credentials, Status, Rank, Guilds);

    public OfflineCluster() {
        super();
    }

    public static OfflineCluster create(){
        return new OfflineCluster();
    }

    @Nonnull
    @Override
    protected EntityAdapter createEntityAdapter() {
        return Adapter;
    }

    @Nonnull
    @Override
    protected Client createRedditClient(@NotNull Credentials credentials) {
        return new OfflineClient();
    }

    @Nonnull
    @Override
    protected Client createPushshiftClient(@NotNull Credentials credentials) {
        return new OfflineClient();
    }
}
