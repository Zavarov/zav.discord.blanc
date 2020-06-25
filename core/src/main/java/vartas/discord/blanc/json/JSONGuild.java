package vartas.discord.blanc.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import vartas.discord.blanc.*;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

//TODO Generate
public class JSONGuild extends Guild {
    public static final String ID = "id";
    public static final String PREFIX = "prefix";
    public static final String CHANNELS = "channels";
    public static final String ROLES = "roles";
    public static final String BLACKLIST = "blacklist";

    public static Guild of(Path guildPath) throws IOException, JSONException {
        return of(guildId -> new JSONGuild(), guildPath);
    }

    public static Guild of(Function<Long, Guild> guildFunction, Path guildPath) throws IOException, JSONException {
        return of(guildFunction, Files.readString(guildPath));
    }

    public static Guild of(String content){
        return of(guildId -> new JSONGuild(), content);
    }

    public static Guild of(Function<Long, Guild> guildFunction, String content){
        return of(guildFunction, new JSONObject(content));
    }

    public static Guild of(JSONObject jsonObject) {
        return of(guildId -> new JSONGuild(), jsonObject);
    }

    public static Guild of(Function<Long, Guild> guildFunction, JSONObject jsonObject){
        Guild jsonGuild = guildFunction.apply(jsonObject.getLong(ID));

        jsonGuild.setId(jsonObject.getLong(ID));

        JSONArray jsonChannels = jsonObject.getJSONArray(CHANNELS);
        for(int i = 0 ; i < jsonChannels.length() ; ++i){
            JSONTextChannel jsonTextChannel = JSONTextChannel.of(jsonChannels.getJSONObject(i));
            jsonGuild.putChannels(jsonTextChannel.getId(), jsonTextChannel);
        }

        JSONArray jsonRoles = jsonObject.getJSONArray(ROLES);
        for(int i = 0 ; i < jsonRoles.length() ; ++i){
            JSONRole jsonRole = JSONRole.of(jsonRoles.getJSONObject(i));
            jsonGuild.putRoles(jsonRole.getId(), jsonRole);
        }

        JSONArray jsonBlacklist = jsonObject.getJSONArray(BLACKLIST);
        for(int i = 0 ; i < jsonBlacklist.length() ; ++i){
            jsonGuild.addBlacklist(jsonBlacklist.getString(i));
        }

        return jsonGuild;
    }

    public static JSONObject of(GuildTOP guild){
        JSONObject jsonGuild = new JSONObject();

        jsonGuild.put(ID ,guild.getId());
        guild.ifPresentPrefix(prefix -> jsonGuild.put(PREFIX, prefix));

        JSONArray jsonChannels = new JSONArray();
        for(TextChannel channel : guild.valuesChannels())
            jsonChannels.put(JSONTextChannel.of(channel));
        jsonGuild.put(CHANNELS, jsonChannels);

        JSONArray jsonRoles = new JSONArray();
        for(Role role : guild.valuesRoles())
            jsonRoles.put(JSONRole.of(role));
        jsonGuild.put(ROLES, jsonRoles);

        JSONArray jsonBlacklist = new JSONArray();
        for(String entry : guild.getBlacklist())
            jsonBlacklist.put(entry);
        jsonGuild.put(BLACKLIST, jsonBlacklist);

        return jsonGuild;
    }

    @Override
    public void leave(){
        throw new UnsupportedOperationException("Not supported for JSON instances");
    }

    @Override
    public boolean canInteract(@Nonnull Member member, @Nonnull Role role) {
        throw new UnsupportedOperationException("Not supported for JSON instances");
    }

    @Override
    public boolean canInteract(@Nonnull Member member, @Nonnull TextChannel textChannel) {
        throw new UnsupportedOperationException("Not supported for JSON instances");
    }
}
