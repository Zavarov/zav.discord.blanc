/*
 * Copyright (c) 2019 Zavarov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package vartas.discord.entities;

import com.google.common.collect.Multimap;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.RoleImpl;
import net.dv8tion.jda.internal.entities.TextChannelImpl;
import org.junit.Before;
import org.junit.Test;
import vartas.discord.AbstractTest;
import vartas.discord.entities.offline.OfflineShard;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationTest extends AbstractTest {
    OfflineShard shard;
    JDAImpl jda;
    GuildImpl guild;
    TextChannelImpl channel;
    RoleImpl role;
    Configuration configuration;

    @Before
    public void setUp(){
        shard = OfflineShard.create(null);
        jda = shard.createJda(0, null);
        guild = new GuildImpl(jda, guildId){
            @Override
            public RoleImpl getRoleById(long id){
                return id == roleId ? role : null;
            }
            @Override
            public TextChannelImpl getTextChannelById(long id){
                return id == channelId ? channel : null;
            }
        };
        channel = new TextChannelImpl(channelId, guild);
        role = new RoleImpl(roleId, guild);

        configuration = new Configuration(guildId);
        configuration.add("role",role);
        configuration.add("channel",channel);
        configuration.setPattern(Pattern.compile("pattern"));
        configuration.setPrefix("prefix");
    }

    @Test
    public void getGuildIdTest(){
        assertThat(configuration.getGuildId()).isEqualTo(guild.getIdLong());
    }

    @Test
    public void getBlacklistTest(){
        assertThat(configuration.getPattern()).map(Pattern::pattern).contains("pattern");
    }

    @Test
    public void setBlacklistTest(){
        assertThat(configuration.getPattern()).map(Pattern::pattern).contains("pattern");
        configuration.setPattern(Pattern.compile("test"));
        assertThat(configuration.getPattern()).map(Pattern::pattern).contains("test");
    }

    @Test
    public void removeBlacklistTest(){
        assertThat(configuration.getPattern()).map(Pattern::pattern).contains("pattern");
        configuration.removePattern();
        assertThat(configuration.getPattern()).isEmpty();
    }

    @Test
    public void getPrefixTest(){
        assertThat(configuration.getPrefix()).contains("prefix");
    }

    @Test
    public void setPrefixTest(){
        assertThat(configuration.getPrefix()).contains("prefix");
        configuration.setPrefix("test");
        assertThat(configuration.getPrefix()).contains("test");
    }

    @Test
    public void removePrefixTest(){
        assertThat(configuration.getPrefix()).contains("prefix");
        configuration.removePrefix();
        assertThat(configuration.getPrefix()).isEmpty();
    }

    @Test
    public void resolveRoleTest(){
        assertThat(configuration.resolve(role)).contains("role");
        assertThat(configuration.resolve("role",role)).isTrue();
        assertThat(configuration.resolve("test",role)).isFalse();
    }

    @Test
    public void resolveAndMapTest(){
        Multimap<String, Role> multimap = configuration.resolve(Configuration.LongType.SELFASSIGNABLE, guild::getRoleById);
        assertThat(multimap.size()).isEqualTo(1);
        assertThat(multimap.containsEntry("role", role)).isTrue();

        configuration.groups.clear();

        multimap = configuration.resolve(Configuration.LongType.SELFASSIGNABLE, guild::getRoleById);
        assertThat(multimap.isEmpty()).isTrue();

        Collection<Role> collection = configuration.resolve(Configuration.LongType.SELFASSIGNABLE, "channel", guild::getRoleById);
        assertThat(collection).isEmpty();
    }

    @Test
    public void removeAllRolesTest(){
        assertThat(configuration.resolve("role", role)).isTrue();
        configuration.remove(role);
        assertThat(configuration.resolve("role", role)).isFalse();
    }

    @Test
    public void removeRoleTest(){
        assertThat(configuration.resolve("role", role)).isTrue();
        configuration.remove("role",role);
        assertThat(configuration.resolve("role", role)).isFalse();
    }

    @Test
    public void addRoleTest(){
        RoleImpl newRole = new RoleImpl(Long.MAX_VALUE, guild);

        assertThat(configuration.resolve("test", newRole)).isFalse();
        configuration.add("test", newRole);
        assertThat(configuration.resolve("test", newRole)).isTrue();
    }

    @Test(expected=IllegalArgumentException.class)
    public void addUsedRoleTest(){
        configuration.add("test", role);
    }

    @Test
    public void resolveChannelTest(){
        assertThat(configuration.resolve(channel)).containsExactly("channel");
        assertThat(configuration.resolve("channel",channel)).isTrue();
        assertThat(configuration.resolve("test",channel)).isFalse();
    }

    @Test
    public void removeAllChannelsTest(){
        assertThat(configuration.resolve("channel", channel)).isTrue();
        configuration.remove("channel", channel);
        assertThat(configuration.resolve("channel", channel)).isFalse();
    }

    @Test
    public void removeChannelTest(){
        assertThat(configuration.resolve("channel", channel)).isTrue();
        configuration.remove(channel);
        assertThat(configuration.resolve("channel", channel)).isFalse();
    }

    @Test
    public void addChannelTest(){
        assertThat(configuration.resolve("test", channel)).isFalse();
        configuration.add("test", channel);
        assertThat(configuration.resolve("test", channel)).isTrue();
    }

    @Test
    public void existsTest(){
        assertThat(Configuration.LongType.SELFASSIGNABLE.exists(guild, roleId)).isTrue();
        assertThat(Configuration.LongType.SELFASSIGNABLE.exists(guild, channelId)).isFalse();
        assertThat(Configuration.LongType.SUBREDDIT.exists(guild, channelId)).isTrue();
        assertThat(Configuration.LongType.SUBREDDIT.exists(guild, roleId)).isFalse();
    }

    @Test
    public void visitorTest(){
        Configuration.Visitor visitor = new Visitor();
        configuration.accept(visitor);
        visitor = new EmptyVisitor();
        configuration.accept(visitor);
    }

    private static class EmptyVisitor implements Configuration.Visitor{}

    private class Visitor implements Configuration.Visitor{
        @Override
        public void visit(@Nonnull Configuration.LongType type, @Nonnull String key, @Nonnull Collection<Long> values){
            if(type == Configuration.LongType.SELFASSIGNABLE){
                assertThat(key).isEqualTo("role");
                assertThat(values).containsExactly(roleId);
            }else{
                assertThat(key).isEqualTo("channel");
                assertThat(values).containsExactly(channelId);
            }
        }
        @Override
        public void traverse(@Nonnull Configuration.LongType type, @Nonnull String key, @Nonnull Collection<Long> values){
            if(type == Configuration.LongType.SELFASSIGNABLE){
                assertThat(key).isEqualTo("role");
                assertThat(values).containsExactly(roleId);
            }else{
                assertThat(key).isEqualTo("channel");
                assertThat(values).containsExactly(channelId);
            }
        }
        @Override
        public void endVisit(@Nonnull Configuration.LongType type, @Nonnull String key, @Nonnull Collection<Long> values){
            if(type == Configuration.LongType.SELFASSIGNABLE){
                assertThat(key).isEqualTo("role");
                assertThat(values).containsExactly(roleId);
            }else{
                assertThat(key).isEqualTo("channel");
                assertThat(values).containsExactly(channelId);
            }
        }
        @Override
        public void visit(@Nonnull Pattern pattern){
            assertThat(pattern.pattern()).isEqualTo("pattern");
        }
        @Override
        public void traverse(@Nonnull Pattern pattern){
            assertThat(pattern.pattern()).isEqualTo("pattern");
        }
        @Override
        public void endVisit(@Nonnull Pattern pattern){
            assertThat(pattern.pattern()).isEqualTo("pattern");
        }
        @Override
        public void visit(@Nonnull String prefix){
            assertThat(prefix).isEqualTo("prefix");
        }
        @Override
        public void traverse(@Nonnull String prefix){
            assertThat(prefix).isEqualTo("prefix");
        }
        @Override
        public void endVisit(@Nonnull String prefix){
            assertThat(prefix).isEqualTo("prefix");
        }
        @Override
        public void visit(@Nonnull Configuration configuration){
            assertThat(configuration).isEqualTo(ConfigurationTest.this.configuration);
        }
        @Override
        public void endVisit(@Nonnull Configuration configuration){
            assertThat(configuration).isEqualTo(ConfigurationTest.this.configuration);
        }
    }
}
