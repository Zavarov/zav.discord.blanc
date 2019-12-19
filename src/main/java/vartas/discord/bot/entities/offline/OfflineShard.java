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
import net.dv8tion.jda.internal.managers.PresenceImpl;
import net.dv8tion.jda.internal.utils.config.AuthorizationConfig;
import vartas.discord.bot.CommandBuilder;
import vartas.discord.bot.EntityAdapter;
import vartas.discord.bot.JSONEntityAdapter;
import vartas.discord.bot.entities.*;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class OfflineShard extends Shard {
    public static final Path credentials = Paths.get("src/test/resources/credentials.json");
    public static final Path status = Paths.get("src/test/resources/status.json");
    public static final Path rank = Paths.get("src/test/resources/rank.json");
    public static final Path guilds = Paths.get("src/test/resources/guilds");

    private final static AuthorizationConfig authorization = new AuthorizationConfig(AccountType.BOT, "12345");
    public List<? super Object> send = new ArrayList<>();
    public List<? super Object> removed = new ArrayList<>();
    public List<? super Object> stored = new ArrayList<>();
    public Cluster cluster = new OfflineCluster();

    public OfflineShard() throws LoginException, InterruptedException {
        super(0);
    }

    @Override
    public void store(Configuration configuration){
        stored.add(configuration.getGuildId());
    }

    @Override
    public void store(Rank rank){
        stored.add(rank);
    }

    @Override
    public void remove(Guild guild){
        removed.add(guild.getIdLong());
    }

    @Override
    public CommandBuilder createCommandBuilder() {
        return new OfflineCommandBuilder();
    }

    @Override
    public EntityAdapter createEntityAdapter() {
        return new JSONEntityAdapter(credentials, status, rank, guilds);
    }

    @Override
    public JDAImpl createJda(int shardId, Credentials credentials) {
        return new JDAImpl(authorization){
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
    protected Cluster createCluster() {
        return cluster;
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
}
