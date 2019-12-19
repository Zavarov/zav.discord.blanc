package vartas.discord.bot.entities.offline;

import org.jetbrains.annotations.NotNull;
import vartas.discord.bot.EntityAdapter;
import vartas.discord.bot.JSONEntityAdapter;
import vartas.discord.bot.entities.Cluster;
import vartas.discord.bot.entities.Credentials;
import vartas.reddit.Client;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.nio.file.Paths;


public class OfflineCluster extends Cluster {
    public static final Path credentials = Paths.get("src/test/resources/credentials.json");
    public static final Path status = Paths.get("src/test/resources/status.json");
    public static final Path rank = Paths.get("src/test/resources/rank.json");
    public static final Path guilds = Paths.get("src/test/resources/guilds");


    public OfflineCluster() {
        super();
    }

    @Nonnull
    @Override
    protected EntityAdapter createEntityAdapter() {
        return new JSONEntityAdapter(credentials, status, rank, guilds);
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
