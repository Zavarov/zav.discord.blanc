package vartas.discord.blanc.json;

import org.json.JSONObject;
import vartas.discord.blanc.MessageEmbed;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//TODO Generate
public class JSONMessageEmbed extends MessageEmbed {
    public static JSONMessageEmbed of(Path messagePath) throws IOException {
        return of(Files.readString(messagePath));
    }

    public static JSONMessageEmbed of(String content){
        return of(new JSONObject(content));
    }

    public static JSONMessageEmbed of(JSONObject jsonObject){
        JSONMessageEmbed jsonMessageEmbed = new JSONMessageEmbed();
        jsonMessageEmbed.setThumbnail(Optional.ofNullable(jsonObject.optString("thumbnail",null)));
        jsonMessageEmbed.setTitle(Optional.ofNullable(jsonObject.optString("title", null)));
        jsonMessageEmbed.setContent(Optional.ofNullable(jsonObject.optString("content", null)));
        jsonMessageEmbed.setTimestamp(Optional.ofNullable(jsonObject.optString("timestamp",null)).map(Instant::parse));
        jsonMessageEmbed.addAllFields(IntStream.range(0, jsonObject.getJSONArray("fields").length()).mapToObj(jsonObject.getJSONArray("fields")::getJSONObject).map(JSONField::of).collect(Collectors.toList()));
        return jsonMessageEmbed;
    }
}
