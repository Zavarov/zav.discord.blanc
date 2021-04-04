
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



 public  class JSONClient   {
      protected  final  static  String SHARDS  = "shards";
     protected  void $fromShards( JSONObject source,  Client target){

        throw new UnsupportedOperationException("Deserialization for `shards` not implemented.");
    }

     protected  void $toShards( Client source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `shards` not implemented.");
    }

     public  static  Client fromJson( Client target,  JSONObject source){

        //Create new instance for deserializer
        JSONClient $json = new JSONClient();
        //Deserialize attributes
        $json.$fromShards(source, target);
        //Return deserialized  object
        return target;
    }

     public  static  Client fromJson( Client target,  Path path)throws IOException{

        return fromJson(target, java.nio.file.Files.readString(path));
    }

     public  static  Client fromJson( Client target,  String content){

        return fromJson(target, new JSONObject(content));
    }

     public  static  JSONObject toJson( Client source,  JSONObject target){

        //Create new instance for serializer
        JSONClient $json = new JSONClient();
        //Serialize attributes
        $json.$toShards(source, target);
        //Return serialized  object
        return target;
    }

}