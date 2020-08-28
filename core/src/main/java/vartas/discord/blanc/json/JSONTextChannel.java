package vartas.discord.blanc.json;

import org.json.JSONArray;
import org.json.JSONObject;
import vartas.discord.blanc.Message;
import vartas.discord.blanc.TextChannel;
import vartas.discord.blanc.Webhook;

import java.util.Map;

//TODO Generate
public class JSONTextChannel extends TextChannel {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String SUBREDDITS = "subreddits";
    public static final String WEBHOOKS = "webhooks";

    public static JSONTextChannel of(JSONObject jsonObject){
        JSONTextChannel jsonTextChannel = new JSONTextChannel();
        jsonTextChannel.setId(jsonObject.getLong(ID));
        jsonTextChannel.setName(jsonObject.getString(NAME));

        JSONArray subreddits = jsonObject.optJSONArray(SUBREDDITS);
        if(subreddits != null)
            for(int i = 0 ; i < subreddits.length() ; ++i)
                jsonTextChannel.addSubreddits(subreddits.getString(i));

        JSONObject webhooks = jsonObject.optJSONObject(WEBHOOKS);
        if(webhooks != null)
            for(String key : webhooks.keySet())
                jsonTextChannel.putWebhooks(key, JSONWebhook.of(webhooks.getJSONObject(key)));

        return jsonTextChannel;
    }

    public static JSONObject of(TextChannel channel){
        JSONObject jsonTextChannel = new JSONObject();

        jsonTextChannel.put(ID, channel.getId());
        jsonTextChannel.put(NAME, channel.getName());

        JSONArray subreddits = new JSONArray();
        for(String subreddit : channel.getSubreddits())
            subreddits.put(subreddit);
        jsonTextChannel.put(SUBREDDITS, subreddits);

        JSONObject webhooks = new JSONObject();
        for(Map.Entry<String, Webhook> webhook : channel.asMapWebhooks().entrySet())
            webhooks.put(webhook.getKey(), JSONWebhook.of(webhook.getValue()));
        jsonTextChannel.put(WEBHOOKS, webhooks);

        return jsonTextChannel;
    }

    @Override
    public void send(Message message){
        throw new UnsupportedOperationException("Not supported for JSON instances");
    }

    @Override
    public void send(byte[] bytes, String qualifiedName) {
        throw new UnsupportedOperationException("Not supported for JSON instances");
    }

    @Override
    public String getAsMention() {
        throw new UnsupportedOperationException();
    }
}
