package vartas.discord.bot.entities.offline;

import org.jetbrains.annotations.NotNull;
import vartas.discord.bot.EntityAdapter;
import vartas.discord.bot.JSONEntityAdapter;
import vartas.discord.bot.Main;
import vartas.discord.bot.entities.Cluster;
import vartas.discord.bot.entities.Credentials;
import vartas.discord.bot.entities.Shard;
import vartas.reddit.Client;


public class OfflineCluster extends Cluster {


    public OfflineCluster(Shard shard) {
        super(shard);
    }

    @NotNull
    @Override
    protected EntityAdapter createEntityAdapter() {
        return new JSONEntityAdapter(Main.credentials, Main.status, Main.rank, Main.guilds);
    }

    @NotNull
    @Override
    protected Client createRedditClient(@NotNull Credentials credentials) {
        return null;
    }

    @NotNull
    @Override
    protected Client createPushshiftClient(@NotNull Credentials credentials) {
        return null;
    }
}
