package vartas.discord.blanc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vartas.discord.blanc.io.Credentials;
import vartas.discord.blanc.json.JSONGuild;
import vartas.discord.blanc.visitor.ArchitectureVisitor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;

public abstract class ShardLoader implements ArchitectureVisitor {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Credentials credentials;
    private final int shardCount;
    protected Function<Long, Guild> defaultGuild;

    public ShardLoader(Credentials credentials){
        this.credentials = credentials;
        this.shardCount = credentials.getShardCount();
        this.defaultGuild = (guildId) -> new JSONGuild();
    }

    public abstract Shard load(int shardId);

    public abstract Optional<Guild> load(@Nonnull Path guildPath);

    @Override
    public void visit(@Nonnull ShardTOP shard){
        Path guildDirectory = credentials.getGuildDirectory();

        try {
            if(Files.notExists(guildDirectory))
                Files.createDirectories(guildDirectory);

            Files.list(guildDirectory)
                    .map(this::load)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(guild -> isInShard(shard, guild))
                    .forEach(guild -> shard.putGuilds(guild.getId(), guild));

        }catch(IOException e){
            log.error(Errors.INVALID_FILE.toString(), e);
        }
    }

    private boolean isInShard(@Nonnull ShardTOP shard, @Nonnull Guild guild){
        return (guild.getId() >> 22) % shardCount == shard.getId();
    }
}
