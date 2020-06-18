package vartas.discord.blanc.json;

import org.json.JSONObject;
import vartas.discord.blanc.Role;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

//TODO Generate
public class JSONRole extends Role {
    public static JSONRole of(Path rolePath) throws IOException {
        return of(Files.readString(rolePath));
    }

    public static JSONRole of(String content){
        return of(new JSONObject(content));
    }

    public static JSONRole of(JSONObject jsonObject){
        JSONRole jsonRole = new JSONRole();
        jsonRole.setGroup(Optional.ofNullable(jsonObject.optString("group")));
        jsonRole.setId(jsonObject.getLong("id"));
        return jsonRole;
    }
}
