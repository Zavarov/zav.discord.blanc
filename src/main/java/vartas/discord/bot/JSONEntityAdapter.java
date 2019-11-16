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

import com.google.common.collect.Multimap;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
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
import java.util.Optional;
import java.util.regex.Pattern;

public class JSONEntityAdapter implements EntityAdapter{
    protected Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    protected Path config;
    protected Path status;
    protected Path rank;
    protected Path guilds;
    public JSONEntityAdapter(Path config, Path status, Path rank, Path guilds){
        this.config = config;
        this.rank = rank;
        this.status = status;
        this.guilds = guilds;
    }

    @Override
    public BotConfig config() {
        JSONObject object = parse(config);
        BotConfig result = new BotConfig();

        result.setType(BotConfig.Type.STATUS_MESSAGE_UPDATE_INTERVAL, object.getInt(BotConfig.Type.STATUS_MESSAGE_UPDATE_INTERVAL.getName()));
        result.setType(BotConfig.Type.INTERACTIVE_MESSAGE_LIFETIME, object.getInt(BotConfig.Type.INTERACTIVE_MESSAGE_LIFETIME.getName()));
        result.setType(BotConfig.Type.ACTIVITY_UPDATE_INTERVAL, object.getInt(BotConfig.Type.ACTIVITY_UPDATE_INTERVAL.getName()));
        result.setType(BotConfig.Type.BOT_NAME, object.getString(BotConfig.Type.BOT_NAME.getName()));
        result.setType(BotConfig.Type.GLOBAL_PREFIX, object.getString(BotConfig.Type.GLOBAL_PREFIX.getName()));
        result.setType(BotConfig.Type.DISCORD_SHARDS, object.getInt(BotConfig.Type.DISCORD_SHARDS.getName()));
        result.setType(BotConfig.Type.IMAGE_WIDTH, object.getInt(BotConfig.Type.IMAGE_WIDTH.getName()));
        result.setType(BotConfig.Type.IMAGE_HEIGHT, object.getInt(BotConfig.Type.IMAGE_HEIGHT.getName()));
        result.setType(BotConfig.Type.INVITE_SUPPORT_SERVER, object.getString(BotConfig.Type.INVITE_SUPPORT_SERVER.getName()));
        result.setType(BotConfig.Type.WIKI_LINK, object.getString(BotConfig.Type.WIKI_LINK.getName()));
        result.setType(BotConfig.Type.DISCORD_TOKEN, object.getString(BotConfig.Type.DISCORD_TOKEN.getName()));
        result.setType(BotConfig.Type.REDDIT_ACCOUNT, object.getString(BotConfig.Type.REDDIT_ACCOUNT.getName()));
        result.setType(BotConfig.Type.REDDIT_ID, object.getString(BotConfig.Type.REDDIT_ID.getName()));
        result.setType(BotConfig.Type.REDDIT_SECRET, object.getString(BotConfig.Type.REDDIT_SECRET.getName()));

        return result;
    }

    @Override
    public BotStatus status() {
        JSONObject object = parse(status);
        BotStatus result = new BotStatus();

        object.getJSONArray("status").toList().stream().map(Object::toString).forEach(result::add);

        return result;
    }

    @Override
    public BotGuild guild(Guild guild, DiscordCommunicator communicator) {
        Path reference = Paths.get(guilds.toString()+ File.separator +guild.getId()+".gld");
        JSONObject object = parse(reference);
        BotGuild result = new BotGuild(guild, communicator, this);

        //Prefix
        try{
            result.set(object.getString("prefix"));
        }catch(JSONException e){
            log.debug(e.getMessage());
        }
        //Pattern
        try{
            result.set(Pattern.compile(object.getString("blacklist")));
        }catch(JSONException e){
            log.debug(e.getMessage());
        }
        //Role groups
        try{
            JSONObject group = object.getJSONObject(BotGuild.ROLEGROUP);

            for(String key : group.keySet()){
                for(Object value : group.getJSONArray(key).toList()){
                    Optional<Role> roleOpt = Optional.ofNullable(guild.getRoleById(value.toString()));

                    roleOpt.ifPresent(role -> result.add(key, role));
                }
            }
        }catch(JSONException e){
            log.debug(e.getMessage());
        }
        //Subreddits
        try{
            JSONObject group = object.getJSONObject(BotGuild.SUBREDDIT);

            for(String key : group.keySet()){
                for(Object value : group.getJSONArray(key).toList()){
                    Optional<TextChannel> channelOpt = Optional.ofNullable(guild.getTextChannelById(value.toString()));

                    channelOpt.ifPresent(channel -> result.add(key, channel));
                }
            }
        }catch(JSONException e){
            log.debug(e.getMessage());
        }

        return result;
    }

    @Override
    public BotRank rank(JDA jda) {
        JSONObject object = parse(rank);
        BotRank result = new BotRank(jda, this);

        for(String key : object.keySet()){
            for(Object value : object.getJSONArray(key).toList()){
                Optional<User> userOpt = Optional.ofNullable(jda.getUserById(key));
                BotRank.Type type = BotRank.Type.valueOf(value.toString());
                userOpt.ifPresent(user -> result.add(user, type));
            }
        }

        return result;
    }

    @Override
    public void store(BotGuild guild) {
        Path reference = Paths.get(guilds.toString()+ File.separator +guild.getId()+".gld");
        JSONObject object = new JSONObject();
        Multimap<String, Long> data;

        guild.prefix().ifPresent(prefix -> object.put("prefix", prefix));
        guild.blacklist().ifPresent(blacklist -> object.put("blacklist", blacklist.pattern()));

        JSONObject roles = new JSONObject();
        object.put(BotGuild.ROLEGROUP, roles);

        data = guild.resolve(BotGuild.ROLEGROUP, (g,l) -> l);
        data.asMap().forEach( (key, values) -> roles.put(key, new JSONArray(values)));

        JSONObject subreddits = new JSONObject();
        object.put(BotGuild.SUBREDDIT, subreddits);

        data = guild.resolve(BotGuild.SUBREDDIT, (g,l) -> l);
        data.asMap().forEach( (key, values) -> subreddits.put(key, new JSONArray(values)));

        store(object, reference);
    }

    @Override
    public void store(BotRank rank) {
        JSONObject object = new JSONObject();

        rank.asMultimap().asMap().forEach( (user, types) -> {
            JSONArray values = new JSONArray();

            types.forEach(type -> values.put(type.toString()));

            object.put(user.getId(), values);
        });

        store(object, this.rank);
    }

    @Override
    public void delete(BotGuild guild) {
        try{
            Path reference = Paths.get("guild/"+guild.getId()+".gld");
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
        JSONObject object;
        try{
            String content = new String(Files.readAllBytes(path));
            return new JSONObject(content);
        }catch(IOException e){
            log.debug(e.getMessage());
            return new JSONObject();
        }
    }
}
