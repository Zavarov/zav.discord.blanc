package vartas.discord.blanc.json;

import org.json.JSONArray;
import org.json.JSONObject;
import vartas.discord.blanc.GuildTOP;
import vartas.discord.blanc.Message;
import vartas.discord.blanc.Role;
import vartas.discord.blanc.TextChannel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

//TODO Generate
public class JSONTextChannel extends TextChannel {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String SUBREDDITS = "subreddits";

    public static JSONTextChannel of(Path textChannelPath) throws IOException {
        return of(Files.readString(textChannelPath));
    }

    public static JSONTextChannel of(String content){
        return of(new JSONObject(content));
    }

    public static JSONTextChannel of(JSONObject jsonObject){
        JSONTextChannel jsonTextChannel = new JSONTextChannel();
        jsonTextChannel.setId(jsonObject.getLong(ID));
        jsonTextChannel.setName(jsonObject.getString(NAME));
        jsonTextChannel.addAllSubreddits(jsonObject.getJSONArray(SUBREDDITS).toList().stream().map(Object::toString).collect(Collectors.toList()));
        return jsonTextChannel;
    }

    public static JSONObject of(TextChannel channel){
        JSONObject jsonTextChannel = new JSONObject();

        jsonTextChannel.put(ID, channel.getId());
        jsonTextChannel.put(NAME, channel.getName());

        JSONArray subreddits = new JSONArray();
        for(String subreddit : channel.getSubreddits())
            subreddits.put(subreddit);
        jsonTextChannel.put(SUBREDDITS, subreddits);

        return jsonTextChannel;
    }

    @Override
    public void send(Message message){
        throw new UnsupportedOperationException("Not supported for JSON instances");
    }

    @Override
    public void send(byte[] bytes, String qualifiedName) {
        throw new UnsupportedOperationException("Not supported for JSON instances");
    }
}
