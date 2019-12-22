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

package vartas.discord.bot.entities.offline;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.managers.PresenceImpl;
import net.dv8tion.jda.internal.utils.config.AuthorizationConfig;
import vartas.discord.bot.CommandBuilder;
import vartas.discord.bot.entities.Configuration;
import vartas.discord.bot.entities.Credentials;
import vartas.discord.bot.entities.Shard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class OfflineShard extends Shard {
    private final static AuthorizationConfig authorization = new AuthorizationConfig(AccountType.BOT, "12345");
    public Map<Long, GuildImpl> guilds = new HashMap<>();
    public Map<Guild, Configuration> configurations = new HashMap<>();
    public List<? super Object> send = new ArrayList<>();
    public List<? super Object> removed = new ArrayList<>();
    public List<? super Object> stored = new ArrayList<>();

    public OfflineShard(OfflineCluster cluster) throws LoginException, InterruptedException {
        super(0, OfflineCluster.Adapter.credentials(), OfflineCluster.Adapter, cluster);
    }

    public static OfflineShard create(OfflineCluster cluster){
        try{
            return new OfflineShard(cluster);
        }catch(LoginException | InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(@Nonnull Guild guild){
        removed.add(guild.getIdLong());
    }

    @Override
    public CommandBuilder createCommandBuilder() {
        return new OfflineCommandBuilder();
    }

    @Override
    public JDAImpl createJda(int shardId, @Nullable Credentials credentials) {
        return new JDAImpl(authorization){
            @Nonnull
            @Override
            public List<Guild> getGuilds(){
                return List.copyOf(guilds.values());
            }
            @Override
            public GuildImpl getGuildById(@Nonnull String id){
                return getGuildById(Long.parseUnsignedLong(id));
            }
            @Override
            public GuildImpl getGuildById(long id){
                return guilds.get(id);
            }
            @Nonnull
            @Override
            public PresenceImpl getPresence(){
                return new PresenceImpl(this){
                    @Override
                    protected void update(DataObject data){
                        send.add(data);
                    }
                };
            }
        };
    }

    @Override
    public <T> void queue(RestAction<T> action, Consumer<? super T> success, Consumer<? super Throwable> failure){
        send.add(action);
    }

    @Override
    public void schedule(Runnable runnable){
        try {
            runnable.run();
        }catch(RuntimeException ignored){}
    }

    @Override
    public Runnable shutdown(){
        return () -> {};
    }

    @Nonnull
    @Override
    public Configuration guild(@Nonnull Guild guild){
        configurations.putIfAbsent(guild, new Configuration(guild.getIdLong()));
        return configurations.get(guild);
    }
}
