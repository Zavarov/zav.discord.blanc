
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



 public  class JSONShard   {
      protected  final  static  String ID  = "id";
     protected  void $fromId( JSONObject source,  Shard target){

        target.setId(source.getInt("id"));

    }

     protected  void $toId( Shard source,  JSONObject target){

        target.put("id", source.getId());

    }

     public  static  Shard fromJson( Shard target,  JSONObject source){

        //Create new instance for deserializer
        JSONShard $json = new JSONShard();
        //Deserialize attributes
        $json.$fromId(source, target);
        //Return deserialized  object
        return target;
    }

     public  static  Shard fromJson( Shard target,  Path path)throws IOException{

        return fromJson(target, java.nio.file.Files.readString(path));
    }

     public  static  Shard fromJson( Shard target,  String content){

        return fromJson(target, new JSONObject(content));
    }

     public  static  JSONObject toJson( Shard source,  JSONObject target){

        //Create new instance for serializer
        JSONShard $json = new JSONShard();
        //Serialize attributes
        $json.$toId(source, target);
        //Return serialized  object
        return target;
    }

}