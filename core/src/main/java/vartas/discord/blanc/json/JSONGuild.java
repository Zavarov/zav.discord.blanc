package vartas.discord.blanc.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import vartas.discord.blanc.Guild;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

//TODO Generate
public class JSONGuild extends Guild {
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
        Guild jsonGuild = guildFunction.apply(jsonObject.getLong("id"));

        jsonGuild.setId(jsonObject.getLong("id"));

        JSONArray jsonChannels = jsonObject.getJSONArray("channels");
        for(int i = 0 ; i < jsonChannels.length() ; ++i){
            JSONTextChannel jsonTextChannel = JSONTextChannel.of(jsonChannels.getJSONObject(i));
            jsonGuild.putChannels(jsonTextChannel.getId(), jsonTextChannel);
        }

        JSONArray jsonRoles = jsonObject.getJSONArray("roles");
        for(int i = 0 ; i < jsonChannels.length() ; ++i){
            JSONRole jsonRole = JSONRole.of(jsonRoles.getJSONObject(i));
            jsonGuild.putRoles(jsonRole.getId(), jsonRole);
        }

        return jsonGuild;
    }
}
