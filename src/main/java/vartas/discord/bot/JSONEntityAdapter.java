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

package vartas.discord.bot;

import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import vartas.discord.bot.entities.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.regex.Pattern;

import static vartas.discord.bot.entities.Configuration.LongType.SELFASSIGNABLE;
import static vartas.discord.bot.entities.Configuration.LongType.SUBREDDIT;
import static vartas.discord.bot.entities.Credentials.IntegerType.*;
import static vartas.discord.bot.entities.Credentials.StringType.*;

public class JSONEntityAdapter implements EntityAdapter{
    protected Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    protected Path credentials;
    protected Path status;
    protected Path rank;
    protected Path configurations;
    public JSONEntityAdapter(Path credentials, Path status, Path rank, Path configurations){
        this.credentials = credentials;
        this.rank = rank;
        this.status = status;
        this.configurations = configurations;
    }

    @Override
    public Credentials credentials() {
        JSONObject object = parse(credentials);
        Credentials result = new Credentials();

        result.setType(STATUS_MESSAGE_UPDATE_INTERVAL, object.getInt(STATUS_MESSAGE_UPDATE_INTERVAL.getName()));
        result.setType(INTERACTIVE_MESSAGE_LIFETIME  , object.getInt(INTERACTIVE_MESSAGE_LIFETIME.getName()));
        result.setType(ACTIVITY_UPDATE_INTERVAL      , object.getInt(ACTIVITY_UPDATE_INTERVAL.getName()));
        result.setType(BOT_NAME                      , object.getString(BOT_NAME.getName()));
        result.setType(GLOBAL_PREFIX                 , object.getString(GLOBAL_PREFIX.getName()));
        result.setType(DISCORD_SHARDS                , object.getInt(DISCORD_SHARDS.getName()));
        result.setType(IMAGE_WIDTH                   , object.getInt(IMAGE_WIDTH.getName()));
        result.setType(IMAGE_HEIGHT                  , object.getInt(IMAGE_HEIGHT.getName()));
        result.setType(INVITE_SUPPORT_SERVER         , object.getString(INVITE_SUPPORT_SERVER.getName()));
        result.setType(WIKI_LINK                     , object.getString(WIKI_LINK.getName()));
        result.setType(DISCORD_TOKEN                 , object.getString(DISCORD_TOKEN.getName()));
        result.setType(REDDIT_ACCOUNT                , object.getString(REDDIT_ACCOUNT.getName()));
        result.setType(REDDIT_ID                     , object.getString(REDDIT_ID.getName()));
        result.setType(REDDIT_SECRET                 , object.getString(REDDIT_SECRET.getName()));

        return result;
    }

    @Override
    public Status status() {
        JSONObject object = parse(status);

        Status result = new Status();

        object.getJSONArray("status").toList().stream().map(Object::toString).forEach(result::add);

        return result;
    }

    @Override
    public Configuration configuration(long guildId, Shard shard) {
        Path reference = Paths.get(configurations.toString()+ File.separator +guildId+".gld");

        if(Files.notExists(reference))
            return new Configuration(guildId, shard);

        JSONObject object = parse(reference);
        Configuration result = new Configuration(guildId, shard);

        //Prefix
        try{
            result.setPrefix(object.getString("prefix"));
        }catch(JSONException e){
            log.debug(e.getMessage());
        }
        //Pattern
        try{
            result.setPattern(Pattern.compile(object.getString("blacklist")));
        }catch(JSONException e){
            log.debug(e.getMessage());
        }
        //Self-Assignable Roles
        try{
            JSONObject group = object.getJSONObject(SELFASSIGNABLE.getName());

            for(String key : group.keySet())
                for(Object value : group.getJSONArray(key).toList())
                    result.add(SELFASSIGNABLE, key, Long.parseUnsignedLong(value.toString()));
        }catch(JSONException e){
            log.debug(e.getMessage());
        }
        //Subreddits
        try{
            JSONObject group = object.getJSONObject(SUBREDDIT.getName());

            for(String key : group.keySet())
                for(Object value : group.getJSONArray(key).toList())
                    result.add(SUBREDDIT, key, Long.parseUnsignedLong(value.toString()));
        }catch(JSONException e){
            log.debug(e.getMessage());
        }

        return result;
    }

    @Override
    public Rank rank() {
        JSONObject object = parse(rank);
        Rank result = new Rank();

        for(String key : object.keySet()){
            for(Object value : object.getJSONArray(key).toList()){
                long user = Long.parseUnsignedLong(key);
                Rank.Ranks ranks = Rank.Ranks.valueOf(value.toString());
                result.add(user, ranks);
            }
        }

        return result;
    }

    @Override
    public void store(Configuration configuration, Guild context) {
        Path reference = Paths.get(configurations.toString()+ File.separator +configuration.getGuildId()+".gld");
        JSONObject object = new JSONObject();
        Multimap<String, Long> data;

        configuration.getPrefix().ifPresent(prefix -> object.put("prefix", prefix));
        configuration.getPattern().ifPresent(blacklist -> object.put("blacklist", blacklist.pattern()));

        JSONObject roles = new JSONObject();
        object.put(SELFASSIGNABLE.getName(), roles);

        data = configuration.resolve(SELFASSIGNABLE);
        data = Multimaps.filterValues(data, value -> SELFASSIGNABLE.exists(context, Preconditions.checkNotNull(value)));
        data.asMap().forEach( (key, values) -> roles.put(key, new JSONArray(values)));

        JSONObject subreddits = new JSONObject();
        object.put(SUBREDDIT.getName(), subreddits);

        data = configuration.resolve(SUBREDDIT);
        data = Multimaps.filterValues(data, value -> SUBREDDIT.exists(context, Preconditions.checkNotNull(value)));
        data.asMap().forEach( (key, values) -> subreddits.put(key, new JSONArray(values)));

        store(object, reference);
    }

    @Override
    public void store(Rank rank) {
        JSONObject object = new JSONObject();

        rank.get().asMap().forEach( (user, types) -> {
            JSONArray values = new JSONArray();

            types.forEach(ranks -> values.put(ranks.toString()));

            object.put(Long.toString(user), values);
        });

        store(object, this.rank);
    }

    @Override
    public void delete(Configuration guild) {
        try{
            Path reference = Paths.get("guild",guild.getGuildId()+".gld");
            Files.deleteIfExists(reference);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    private void store(JSONObject object, Path path){
        try{
            //If the file is in the root folder, getParent() returns null
            if(Objects.nonNull(path.getParent()) && Files.notExists(path.getParent()))
                Files.createDirectories(path.getParent());

            Files.write(path, object.toString().getBytes());
        }catch(IOException e){
            throw new IllegalArgumentException(e);
        }
    }

    private JSONObject parse(Path path){
        try{
            String content = new String(Files.readAllBytes(path));
            return new JSONObject(content);
        }catch(IOException e){
            log.debug(e.getMessage());
            return new JSONObject();
        }
    }
}
