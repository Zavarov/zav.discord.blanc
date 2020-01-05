package vartas.discord.bot.internal;

import com.google.common.base.Preconditions;
import vartas.discord.bot.EntityAdapter;
import vartas.discord.bot.entities.Cluster;
import vartas.discord.bot.entities.Configuration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Nonnull
public class DeleteConfigurationFile implements Cluster.Visitor{
    @Nullable
    private EntityAdapter adapter;
    @Nullable
    private Configuration configuration;

    private final long guildId;

    public DeleteConfigurationFile(long guildId){
        this.guildId = guildId;
    }

    @Override
    public void visit(@Nonnull EntityAdapter adapter){
        this.adapter = adapter;
    }

    @Override
    public void visit(@Nonnull Configuration configuration){
        if(configuration.getGuildId() == guildId)
            this.configuration = configuration;
    }

    @Override
    public void endVisit(@Nonnull Cluster cluster) throws NullPointerException{
        Preconditions.checkNotNull(adapter);
        Preconditions.checkNotNull(configuration);
        adapter.delete(configuration);
    }
}
