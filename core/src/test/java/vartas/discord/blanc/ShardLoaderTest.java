package vartas.discord.blanc;

import com.google.common.cache.Cache;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vartas.discord.blanc.json.JSONGuild;
import vartas.discord.blanc.mock.SelfMemberMock;
import vartas.discord.blanc.mock.ShardLoaderMock;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ShardLoaderTest extends AbstractTest {
    public ShardLoader shardLoader;
    public Shard shard;
    public Guild guild;
    public Cache<Long, Guild> guilds;
    public Path guildDirectory;
    public Path notADirectory;
    public Path notAGuild;
    @BeforeEach
    public void setUp() throws IOException {
        guild = JSONGuild.of(RESOURCES.resolve("guild.json"));
        Shard.write(guild);

        shardLoader = new ShardLoaderMock(credentials);
        shard = shardLoader.load(0);
        guilds = shard.getGuilds();

        notADirectory = RESOURCES.resolve("notadirectory");
        notAGuild = RESOURCES.resolve("invalid");
        guildDirectory = credentials.getGuildDirectory();
    }

    @AfterEach
    public void tearDown(){
        credentials.setGuildDirectory(guildDirectory);
    }

    @Test
    public void testLoad(){
        assertThat(shard.getId()).isEqualTo(0);
        assertThat(guilds.size()).isEqualTo(1);
    }

    @Test
    public void testInvalidDirectory(){
        shard.invalidateAllGuilds();
        credentials.setGuildDirectory(notADirectory);

        shardLoader.load(0);
        assertThat(shard.valuesGuilds()).isEmpty();
    }

    @Test
    public void testInvalidFile(){
        credentials.setGuildDirectory(notAGuild);
        shard = shardLoader.load(0);
        guilds = shard.getGuilds();

        assertThat(shard.getId()).isEqualTo(0);
        assertThat(guilds.size()).isEqualTo(0);
    }

    @Test
    public void testVisit(){
        Map<Long, Guild> guilds;

        shard.accept(shardLoader);
        guilds = shard.getGuilds().asMap();
        assertThat(guilds).hasSize(1);
        assertThat(guilds.containsKey(guild.getId()));
        assertThat(guilds.get(guild.getId()).valuesChannels()).hasSize(1);
        assertThat(guilds.get(guild.getId()).valuesRoles()).hasSize(1);
    }
}
