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

import mpi.MPIException;
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
import vartas.discord.bot.Main;
import vartas.discord.bot.entities.Cluster;
import vartas.discord.bot.entities.Configuration;
import vartas.discord.bot.entities.Rank;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.mpi.MPIAdapter;
import vartas.discord.bot.mpi.command.MPICommand;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class OfflineShard extends Shard {
    private final static AuthorizationConfig authorization = new AuthorizationConfig(AccountType.BOT, "12345");
    public List<? super Object> send = new ArrayList<>();
    public List<? super Object> removed = new ArrayList<>();
    public List<? super Object> stored = new ArrayList<>();

    public OfflineShard() throws MPIException {
        super(new String[]{});
    }

    @Override
    public void store(Configuration configuration, Guild context){
        stored.add(context.getIdLong());
    }

    @Override
    public void store(Rank rank){
        stored.add(rank);
    }

    @Override
    public void remove(long guildId){
        removed.add(guildId);
    }

    @Override
    public CommandBuilder createCommandBuilder() {
        return new OfflineCommandBuilder();
    }

    @Override
    public EntityAdapter createEntityAdapter() {
        return new JSONEntityAdapter(Main.credentials, Main.status, Main.rank, Main.guilds);
    }

    @Override
    public JDAImpl createJda() {
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
    public Cluster createCluster() {
        if(myRank == MPIAdapter.MPI_MASTER_NODE)
            return new OfflineCluster(this);
        else
            return null;
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
    public <T extends Serializable> void send(int shardId, MPICommand<T> command, T object){
        try {
            //Sending to oneself would cause a deadlock
            if(shardId == myRank)
                command.accept(this, object);
            else
                command.send(shardId, object);
        }catch(MPIException ignored){}
    }
}
