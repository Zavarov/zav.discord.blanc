package vartas.discord.blanc.json;

import org.json.JSONArray;
import org.json.JSONObject;
import vartas.discord.blanc.Role;
import vartas.discord.blanc.RoleTOP;
import vartas.discord.blanc.TextChannel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

//TODO Generate
public class JSONRole extends Role {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String GROUP = "group";

    public static JSONRole of(Path rolePath) throws IOException {
        return of(Files.readString(rolePath));
    }

    public static JSONRole of(String content){
        return of(new JSONObject(content));
    }

    public static JSONRole of(JSONObject jsonObject){
        JSONRole jsonRole = new JSONRole();
        jsonRole.setId(jsonObject.getLong(ID));
        jsonRole.setName(jsonObject.getString(NAME));
        jsonRole.setGroup(Optional.ofNullable(jsonObject.optString(GROUP)));
        return jsonRole;
    }

    public static JSONObject of(RoleTOP role){
        JSONObject jsonRole = new JSONObject();

        jsonRole.put(ID, role.getId());
        jsonRole.put(NAME, role.getName());
        role.ifPresentGroup(group -> jsonRole.put(GROUP, group));

        return jsonRole;
    }
}
