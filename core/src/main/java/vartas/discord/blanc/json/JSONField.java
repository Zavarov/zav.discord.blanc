package vartas.discord.blanc.json;

import org.json.JSONObject;
import vartas.discord.blanc.Field;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

//TODO Generate
public class JSONField extends Field {
    public static JSONField of(Path fieldPath) throws IOException {
        return of(Files.readString(fieldPath));
    }

    public static JSONField of(String content){
        return of(new JSONObject(content));
    }

    public static JSONField of(JSONObject jsonObject){
        JSONField jsonField = new JSONField();
        jsonField.setTitle(jsonObject.getString("title"));
        jsonField.setContent(jsonObject.getString("content"));
        return jsonField;
    }
}
