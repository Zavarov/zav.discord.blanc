
package zav.discord.blanc._json;

import com.google.common.cache.Cache;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.CharSequence;
import java.lang.Class;
import java.lang.Comparable;
import java.lang.InterruptedException;
import java.lang.Object;
import java.lang.RuntimeException;
import java.lang.String;
import java.lang.StringBuffer;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.json.JSONObject;
import zav.discord.blanc.*;
import zav.discord.blanc._visitor.ArchitectureVisitor;
import zav.discord.blanc.activity.Activity;
import zav.jra.models.AbstractLink;
import zav.jra.models.AbstractSubreddit;



 public  class JSONMember   {
      protected  final  static  String ONLINESTATUS  = "onlineStatus";
      protected  final  static  String NAME  = "name";
      protected  final  static  String ID  = "id";
      protected  final  static  String RANKS  = "ranks";
     protected  void $fromId( JSONObject source,  Member target){

        target.setId(source.getLong("id"));

    }

     protected  void $fromName( JSONObject source,  Member target){

        target.setName(source.getString("name"));

    }

     protected  void $fromOnlineStatus( JSONObject source,  Member target){

        throw new UnsupportedOperationException("Deserialization for `onlineStatus` not implemented.");
    }

     protected  void $fromRanks( JSONObject source,  Member target){

        throw new UnsupportedOperationException("Deserialization for `ranks` not implemented.");
    }

     protected  void $toId( Member source,  JSONObject target){

        target.put("id", source.getId());

    }

     protected  void $toName( Member source,  JSONObject target){

        target.put("name", source.getName());

    }

     protected  void $toOnlineStatus( Member source,  JSONObject target){

        target.put("onlineStatus", JSONOnlineStatus.toJson(source.getOnlineStatus(), new JSONObject()));

    }

     protected  void $toRanks( Member source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `ranks` not implemented.");
    }

     public  static  Member fromJson( Member target,  JSONObject source){

        //Create new instance for deserializer
        JSONMember $json = new JSONMember();
        //Deserialize attributes
        $json.$fromId(source, target);
        $json.$fromName(source, target);
        $json.$fromOnlineStatus(source, target);
        $json.$fromRanks(source, target);
        //Return deserialized  object
        return target;
    }

     public  static  Member fromJson( Member target,  Path path)throws IOException{

        return fromJson(target, java.nio.file.Files.readString(path));
    }

     public  static  Member fromJson( Member target,  String content){

        return fromJson(target, new JSONObject(content));
    }

     public  static  JSONObject toJson( Member source,  JSONObject target){

        //Create new instance for serializer
        JSONMember $json = new JSONMember();
        //Serialize attributes
        $json.$toId(source, target);
        $json.$toName(source, target);
        $json.$toOnlineStatus(source, target);
        $json.$toRanks(source, target);
        //Return serialized  object
        return target;
    }

}