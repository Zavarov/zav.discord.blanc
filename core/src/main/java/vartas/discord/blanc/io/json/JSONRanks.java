package vartas.discord.blanc.io.json;

import org.json.JSONObject;
import vartas.discord.blanc.io.Ranks;
import vartas.discord.blanc.json.JSONUser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//TODO Generate
public class JSONRanks extends Ranks {
    public static JSONRanks of(Path guildPath) throws IOException {
        return of(Files.readString(guildPath));
    }

    public static JSONRanks of(String content){
        return of(new JSONObject(content));
    }

    public static JSONRanks of(JSONObject jsonObject){
        JSONRanks jsonRanks = new JSONRanks();
        jsonRanks.addAllRanks(IntStream.range(0, jsonObject.getJSONArray("ranks").length()).mapToObj(jsonObject.getJSONArray("ranks")::getJSONObject).map(JSONUser::of).collect(Collectors.toList()));
        return jsonRanks;
    }
}
