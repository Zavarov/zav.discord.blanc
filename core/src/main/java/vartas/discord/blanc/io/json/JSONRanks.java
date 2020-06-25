package vartas.discord.blanc.io.json;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import vartas.discord.blanc.Rank;
import vartas.discord.blanc.io.Ranks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//TODO Generate
public class JSONRanks extends Ranks {
    public static JSONRanks RANKS ;

    static{
        try{
            RANKS = JSONRanks.of(Paths.get("ranks.json"));
        }catch(IOException e){
            LoggerFactory.getLogger(JSONRanks.class.getSimpleName()).error(e.toString());
            RANKS = new JSONRanks();
        }
    }

    public static JSONRanks of(Path guildPath) throws IOException {
        return of(Files.readString(guildPath));
    }

    public static JSONRanks of(String content){
        return of(new JSONObject(content));
    }

    //- Map<Long, Set<Rank>> ranks;

    public static JSONRanks of(JSONObject jsonObject){
        JSONRanks jsonRanks = new JSONRanks();
        Multimap<Long, Rank> ranks = HashMultimap.create();

        for(String key : jsonObject.keySet()){
            JSONArray values = jsonObject.getJSONArray(key);

            for(int i = 0 ; i < values.length() ; ++i)
                ranks.put(Long.parseUnsignedLong(key), values.getEnum(Rank.class, i));
        }

        jsonRanks.setRanks(ranks);
        return jsonRanks;
    }

    public static JSONObject of(Ranks ranks){
        JSONObject jsonObject = new JSONObject();

        ranks.getRanks().asMap().forEach((key, values) -> {
            JSONArray jsonRanks = new JSONArray();
            values.forEach(jsonRanks::put);
            jsonObject.put(Long.toUnsignedString(key), jsonRanks);
        });

        return jsonObject;
    }
}
