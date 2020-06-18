package vartas.discord.blanc.io.json;

import org.json.JSONObject;
import vartas.discord.blanc.io.Credentials;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//TODO Generate
public class JSONCredentials extends Credentials {
    public static JSONCredentials of(Path guildPath) throws IOException {
        return of(Files.readString(guildPath));
    }

    public static JSONCredentials of(String content){
        return of(new JSONObject(content));
    }

    public static JSONCredentials of(JSONObject jsonObject){
        JSONCredentials jsonCredentials = new JSONCredentials();

        jsonCredentials.setStatusMessageUpdateInterval(jsonObject.getInt("statusMessageUpdateInterval"));
        jsonCredentials.setInteractiveMessageLifetime(jsonObject.getInt("interactiveMessageLifetime"));

        jsonCredentials.setBotName(jsonObject.getString("botName"));
        jsonCredentials.setGlobalPrefix(jsonObject.getString("globalPrefix"));
        jsonCredentials.setShardCount(jsonObject.getInt("shardCount"));
        jsonCredentials.setImageWidth(jsonObject.getInt("imageWidth"));


        jsonCredentials.setInviteSupportServer(jsonObject.getString("inviteSupportServer"));
        jsonCredentials.setWikiUrl(jsonObject.getString("wikiUrl"));
        jsonCredentials.setDiscordToken(jsonObject.getString("discordToken"));
        jsonCredentials.setRedditAccount(jsonObject.getString("redditAccount"));
        jsonCredentials.setRedditId(jsonObject.getString("redditId"));
        jsonCredentials.setRedditSecret(jsonObject.getString("redditSecret"));

        jsonCredentials.setGuildDirectory(Paths.get(jsonObject.getString("guildDirectory")));

        return jsonCredentials;
    }
}
