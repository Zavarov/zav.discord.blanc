package vartas.discord.blanc.json;

import org.json.JSONObject;
import vartas.discord.blanc.Message;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;

//TODO Generate
public class JSONMessage extends Message {
    public static JSONMessage of(Path messagePath) throws IOException {
        return of(Files.readString(messagePath));
    }

    public static JSONMessage of(String content){
        return of(new JSONObject(content));
    }

    public static JSONMessage of(JSONObject jsonObject){
        JSONMessage jsonMessage = new JSONMessage();
        jsonMessage.setId(jsonObject.getLong("id"));
        jsonMessage.setCreated(Instant.parse(jsonObject.getString("created")));
        jsonMessage.setAuthor(JSONUser.of(jsonObject.getJSONObject("author")));
        jsonMessage.setContent(Optional.ofNullable(jsonObject.getString("content")));
        jsonMessage.setMessageEmbed(Optional.ofNullable(jsonObject.getJSONObject("messageEmbed")).map(JSONMessageEmbed::of));
        return jsonMessage;
    }
}
