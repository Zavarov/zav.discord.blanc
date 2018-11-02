/*
 * Copyright (C) 2017 u/Zavarov
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

package vartas;

import com.neovisionaries.ws.client.WebSocketFactory;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.managers.impl.PresenceImpl;
import net.dv8tion.jda.core.utils.cache.CacheFlag;
import okhttp3.OkHttpClient;

/**
 * @author u/Zavarov
 */
public class OfflineJDA extends JDAImpl{
    PresenceImpl presence;
    public OfflineJDA() {
        super(AccountType.BOT, null, null, new OkHttpClient.Builder().build(), new WebSocketFactory(),
        Executors.newSingleThreadScheduledExecutor(), Executors.newSingleThreadScheduledExecutor(), Executors.newSingleThreadExecutor(),
        false, false, false,
        false, false, false,
        false, false, false,
        0,0,
        new ConcurrentHashMap<>(), EnumSet.of(CacheFlag.GAME));
        
        presence = new OfflinePresence(OfflineJDA.this);
    }
    
    @Override
    public PresenceImpl getPresence(){
        return presence;
    }
    @Override
    public void shutdown(){
        status = JDA.Status.DISCONNECTED;
    }
    @Override
    public JDA.ShardInfo getShardInfo(){
        List<JDA.ShardInfo> info = new ArrayList<>();
        JDABuilder builder = new JDABuilder(AccountType.BOT){
            @Override
            public JDABuilder useSharding(int start, int end){
                JDABuilder builder = super.useSharding(start, end);
                info.add(shardInfo);
                return builder;
            }
        };
        builder.useSharding(0, 1);
        return info.get(0);
    }
    @Override
    public JDA awaitStatus(Status status){
        this.setStatus(status);
        return this;
    }
}