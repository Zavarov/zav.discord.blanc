package vartas.discord.bot.entities.offline;

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

    @Override
    protected EntityAdapter createEntityAdapter() {
        return new JSONEntityAdapter(Main.credentials, Main.status, Main.rank, Main.guilds);
    }

    @Override
    protected Client createRedditClient(Credentials credentials) {
        return null;
    }

    @Override
    protected Client createPushshiftClient(Credentials credentials) {
        return null;
    }
}
