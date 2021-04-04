
package zav.discord.blanc.activity._json;

import com.google.common.cache.Cache;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.lang.CharSequence;
import java.lang.Class;
import java.lang.Comparable;
import java.lang.Double;
import java.lang.InterruptedException;
import java.lang.Object;
import java.lang.String;
import java.lang.StringBuffer;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.json.JSONObject;
import zav.discord.blanc.Guild;
import zav.discord.blanc.TextChannel;
import zav.discord.blanc.activity.*;
import zav.discord.blanc.activity._visitor.ActivityVisitor;



 public  class JSONGuildActivity   {
      protected  final  static  String MEMBERSCOUNT  = "membersCount";
      protected  final  static  String ACTIVITY  = "activity";
      protected  final  static  String MEMBERSONLINE  = "membersOnline";
      protected  final  static  String CHANNELACTIVITY  = "channelActivity";
     protected  void $fromActivity( JSONObject source,  GuildActivity target){

        target.setActivity(source.getDouble("activity"));

    }

     protected  void $fromChannelActivity( JSONObject source,  GuildActivity target){

        throw new UnsupportedOperationException("Deserialization for `channelActivity` not implemented.");
    }

     protected  void $fromMembersCount( JSONObject source,  GuildActivity target){

        target.setMembersCount(source.getLong("membersCount"));

    }

     protected  void $fromMembersOnline( JSONObject source,  GuildActivity target){

        target.setMembersOnline(source.getLong("membersOnline"));

    }

     protected  void $toActivity( GuildActivity source,  JSONObject target){

        target.put("activity", source.getActivity());

    }

     protected  void $toChannelActivity( GuildActivity source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `channelActivity` not implemented.");
    }

     protected  void $toMembersCount( GuildActivity source,  JSONObject target){

        target.put("membersCount", source.getMembersCount());

    }

     protected  void $toMembersOnline( GuildActivity source,  JSONObject target){

        target.put("membersOnline", source.getMembersOnline());

    }

     public  static  GuildActivity fromJson( GuildActivity target,  JSONObject source){

        //Create new instance for deserializer
        JSONGuildActivity $json = new JSONGuildActivity();
        //Deserialize attributes
        $json.$fromActivity(source, target);
        $json.$fromChannelActivity(source, target);
        $json.$fromMembersCount(source, target);
        $json.$fromMembersOnline(source, target);
        //Return deserialized  object
        return target;
    }

     public  static  GuildActivity fromJson( GuildActivity target,  Path path)throws IOException{

        return fromJson(target, java.nio.file.Files.readString(path));
    }

     public  static  GuildActivity fromJson( GuildActivity target,  String content){

        return fromJson(target, new JSONObject(content));
    }

     public  static  JSONObject toJson( GuildActivity source,  JSONObject target){

        //Create new instance for serializer
        JSONGuildActivity $json = new JSONGuildActivity();
        //Serialize attributes
        $json.$toActivity(source, target);
        $json.$toChannelActivity(source, target);
        $json.$toMembersCount(source, target);
        $json.$toMembersOnline(source, target);
        //Return serialized  object
        return target;
    }

}