package vartas.discord.blanc.json;

import org.json.JSONObject;
import vartas.discord.blanc.Rank;
import vartas.discord.blanc.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

//TODO Generate
public class JSONUser extends User {
    public static JSONUser of(Path userPath) throws IOException {
        return of(Files.readString(userPath));
    }

    public static JSONUser of(String content){
        return of(new JSONObject(content));
    }

    public static JSONUser of(JSONObject jsonObject){
        JSONUser jsonUser = new JSONUser();
        jsonUser.setId(jsonObject.getLong("id"));
        jsonUser.setRank(jsonObject.getEnum(Rank.class, "rank"));
        return jsonUser;
    }

    public static JSONObject toJSON(User user){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id",user.getId());
        jsonObject.put("rank",user.getRank().name());
        return jsonObject;
    }
}
