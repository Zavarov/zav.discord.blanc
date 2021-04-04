
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



 public  class JSONField   {
      protected  final  static  String CONTENT  = "content";
      protected  final  static  String INLINE  = "inline";
      protected  final  static  String TITLE  = "title";
     protected  void $fromContent( JSONObject source,  Field target){

        throw new UnsupportedOperationException("Deserialization for `content` not implemented.");
    }

     protected  void $fromInline( JSONObject source,  Field target){

        target.setInline(source.getBoolean("inline"));

    }

     protected  void $fromTitle( JSONObject source,  Field target){

        target.setTitle(source.getString("title"));

    }

     protected  void $toContent( Field source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `content` not implemented.");
    }

     protected  void $toInline( Field source,  JSONObject target){

        target.put("inline", source.getInline());

    }

     protected  void $toTitle( Field source,  JSONObject target){

        target.put("title", source.getTitle());

    }

     public  static  Field fromJson( Field target,  JSONObject source){

        //Create new instance for deserializer
        JSONField $json = new JSONField();
        //Deserialize attributes
        $json.$fromContent(source, target);
        $json.$fromInline(source, target);
        $json.$fromTitle(source, target);
        //Return deserialized  object
        return target;
    }

     public  static  Field fromJson( Field target,  Path path)throws IOException{

        return fromJson(target, java.nio.file.Files.readString(path));
    }

     public  static  Field fromJson( Field target,  String content){

        return fromJson(target, new JSONObject(content));
    }

     public  static  JSONObject toJson( Field source,  JSONObject target){

        //Create new instance for serializer
        JSONField $json = new JSONField();
        //Serialize attributes
        $json.$toContent(source, target);
        $json.$toInline(source, target);
        $json.$toTitle(source, target);
        //Return serialized  object
        return target;
    }

}