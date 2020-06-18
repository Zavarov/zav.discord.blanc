package vartas.discord.blanc.io.json;

import org.json.JSONObject;
import vartas.discord.blanc.io.StatusMessages;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

//TODO Generate
public class JSONStatusMessages extends StatusMessages {
    public static JSONStatusMessages of(Path guildPath) throws IOException {
        return of(Files.readString(guildPath));
    }

    public static JSONStatusMessages of(String content){
        return of(new JSONObject(content));
    }

    public static JSONStatusMessages of(JSONObject jsonObject){
        JSONStatusMessages jsonStatusMessages = new JSONStatusMessages();
        jsonStatusMessages.addAllStatusMessages(jsonObject.getJSONArray("statusMessages").toList().stream().map(Object::toString).collect(Collectors.toList()));
        return jsonStatusMessages;
    }
}
