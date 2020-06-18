package vartas.discord.blanc.json;

import org.json.JSONObject;
import vartas.discord.blanc.TextChannel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

//TODO Generate
public class JSONTextChannel extends TextChannel {
    public static JSONTextChannel of(Path textChannelPath) throws IOException {
        return of(Files.readString(textChannelPath));
    }

    public static JSONTextChannel of(String content){
        return of(new JSONObject(content));
    }

    public static JSONTextChannel of(JSONObject jsonObject){
        JSONTextChannel jsonTextChannel = new JSONTextChannel();
        jsonTextChannel.setId(jsonObject.getLong("id"));
        jsonTextChannel.setName(jsonObject.getString("name"));
        jsonTextChannel.addAllSubreddits(jsonObject.getJSONArray("subreddits").toList().stream().map(Object::toString).collect(Collectors.toList()));
        return jsonTextChannel;
    }
}
