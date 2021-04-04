
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



 public  class JSONAuthor   {
      protected  final  static  String NAME  = "name";
      protected  final  static  String URL  = "url";
     protected  void $fromName( JSONObject source,  Author target){

        target.setName(source.getString("name"));

    }

     protected  void $fromUrl( JSONObject source,  Author target){

        target.setUrl(source.optString("url", "").isBlank() ? Optional.empty() : Optional.of(source.getString("url")));

    }

     protected  void $toName( Author source,  JSONObject target){

        target.put("name", source.getName());

    }

     protected  void $toUrl( Author source,  JSONObject target){

        source.ifPresentUrl(
            $value -> target.put("url", $value)
        );

    }

     public  static  Author fromJson( Author target,  JSONObject source){

        //Create new instance for deserializer
        JSONAuthor $json = new JSONAuthor();
        //Deserialize attributes
        $json.$fromName(source, target);
        $json.$fromUrl(source, target);
        //Return deserialized  object
        return target;
    }

     public  static  Author fromJson( Author target,  Path path)throws IOException{

        return fromJson(target, java.nio.file.Files.readString(path));
    }

     public  static  Author fromJson( Author target,  String content){

        return fromJson(target, new JSONObject(content));
    }

     public  static  JSONObject toJson( Author source,  JSONObject target){

        //Create new instance for serializer
        JSONAuthor $json = new JSONAuthor();
        //Serialize attributes
        $json.$toName(source, target);
        $json.$toUrl(source, target);
        //Return serialized  object
        return target;
    }

}