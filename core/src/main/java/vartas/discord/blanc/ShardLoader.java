package vartas.discord.blanc;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vartas.discord.blanc.factory.ShardFactory;
import vartas.discord.blanc.io.Credentials;
import vartas.discord.blanc.json.JSONGuild;
import vartas.discord.blanc.visitor.ArchitectureVisitor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;

public class ShardLoader implements ArchitectureVisitor {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Credentials credentials;
    private final int shardCount;
    protected Function<Long, Guild> defaultGuild;

    public ShardLoader(Credentials credentials){
        this.credentials = credentials;
        this.shardCount = credentials.getShardCount();
        this.defaultGuild = (guildId) -> new JSONGuild();
    }

    public Shard load(int shardId){
        log.info("Visiting shard {}/{}.", shardId, shardCount);
        Shard shard = ShardFactory.create(shardId);
        shard.accept(this);
        return shard;
    }

    @Override
    public void visit(@Nonnull ShardTOP shard){
        Path guildDirectory = credentials.getGuildDirectory();
        try {
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

    @Nonnull
    private Optional<Guild> load(@Nonnull Path guildPath){
        try{
            log.info("Loading json file {}.", guildPath);
            return Optional.of(JSONGuild.of(defaultGuild, guildPath));
        }catch(IOException e){
            log.error(Errors.INVALID_FILE.toString(), e);
            return Optional.empty();
        }catch(JSONException e){
            log.error(Errors.INVALID_JSON_FILE.toString(), e);
            return Optional.empty();
        }
    }

    private boolean isInShard(@Nonnull ShardTOP shard, @Nonnull Guild guild){
        return (guild.getId() >> 22) % shardCount == shard.getId();
    }
}
