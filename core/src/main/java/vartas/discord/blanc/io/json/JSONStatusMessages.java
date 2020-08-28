package vartas.discord.blanc.io.json;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import vartas.discord.blanc.Errors;
import vartas.discord.blanc.io.StatusMessages;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

//TODO Generate
public class JSONStatusMessages extends StatusMessages {
    public static JSONStatusMessages STATUS_MESSAGES;

    static{
        try{
            STATUS_MESSAGES = JSONStatusMessages.of(Paths.get("status.json"));
        }catch(IOException e){
            LoggerFactory.getLogger(JSONStatusMessages.class.getSimpleName()).error(Errors.INVALID_FILE.toString(), e.toString());
            STATUS_MESSAGES = new JSONStatusMessages();
        }
    }

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
