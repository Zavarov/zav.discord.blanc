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

package vartas.discord.entities.offline;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.managers.PresenceImpl;
import net.dv8tion.jda.internal.utils.config.AuthorizationConfig;
import vartas.discord.CommandBuilder;
import vartas.discord.entities.Credentials;
import vartas.discord.entities.Shard;

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
    public List<? super Object> send = new ArrayList<>();
    public OfflineCommandBuilder Builder = new OfflineCommandBuilder();

    public OfflineShard(@Nonnull OfflineCluster cluster) throws LoginException, InterruptedException {
        super(0, OfflineCluster.Adapter.credentials(), OfflineCluster.Adapter, cluster);
    }

    public static OfflineShard create(OfflineCluster cluster){
        try{
            return new OfflineShard(cluster);
        }catch(LoginException | InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    @Override
    public CommandBuilder createCommandBuilder() {
        return Builder;
    }

    @Nonnull
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
    public <T> void queue(@Nonnull RestAction<T> action, @Nullable Consumer<? super T> success, @Nullable Consumer<? super Throwable> failure){
        send.add(action);
    }

    @Override
    public void schedule(@Nonnull Runnable runnable){
        try {
            runnable.run();
        }catch(RuntimeException ignored){}
    }

    @Nonnull
    @Override
    public Runnable shutdown(){
        return () -> {};
    }
}
