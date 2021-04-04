
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



 public  class JSONMessageEmbed   {
      protected  final  static  String TIMESTAMP  = "timestamp";
      protected  final  static  String AUTHOR  = "author";
      protected  final  static  String COLOR  = "color";
      protected  final  static  String FIELDS  = "fields";
      protected  final  static  String CONTENT  = "content";
      protected  final  static  String THUMBNAIL  = "thumbnail";
      protected  final  static  String TITLE  = "title";
     protected  void $fromAuthor( JSONObject source,  MessageEmbed target){

        throw new UnsupportedOperationException("Deserialization for `author` not implemented.");
    }

     protected  void $fromColor( JSONObject source,  MessageEmbed target){

        throw new UnsupportedOperationException("Deserialization for `color` not implemented.");
    }

     protected  void $fromContent( JSONObject source,  MessageEmbed target){

        target.setContent(source.optString("content", "").isBlank() ? Optional.empty() : Optional.of(source.getString("content")));

    }

     protected  void $fromFields( JSONObject source,  MessageEmbed target){

        throw new UnsupportedOperationException("Deserialization for `fields` not implemented.");
    }

     protected  void $fromThumbnail( JSONObject source,  MessageEmbed target){

        target.setThumbnail(source.optString("thumbnail", "").isBlank() ? Optional.empty() : Optional.of(source.getString("thumbnail")));

    }

     protected  void $fromTimestamp( JSONObject source,  MessageEmbed target){

        throw new UnsupportedOperationException("Deserialization for `timestamp` not implemented.");
    }

     protected  void $fromTitle( JSONObject source,  MessageEmbed target){

        throw new UnsupportedOperationException("Deserialization for `title` not implemented.");
    }

     protected  void $toAuthor( MessageEmbed source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `author` not implemented.");
    }

     protected  void $toColor( MessageEmbed source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `color` not implemented.");
    }

     protected  void $toContent( MessageEmbed source,  JSONObject target){

        source.ifPresentContent(
            $value -> target.put("content", $value)
        );

    }

     protected  void $toFields( MessageEmbed source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `fields` not implemented.");
    }

     protected  void $toThumbnail( MessageEmbed source,  JSONObject target){

        source.ifPresentThumbnail(
            $value -> target.put("thumbnail", $value)
        );

    }

     protected  void $toTimestamp( MessageEmbed source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `timestamp` not implemented.");
    }

     protected  void $toTitle( MessageEmbed source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `title` not implemented.");
    }

     public  static  MessageEmbed fromJson( MessageEmbed target,  JSONObject source){

        //Create new instance for deserializer
        JSONMessageEmbed $json = new JSONMessageEmbed();
        //Deserialize attributes
        $json.$fromAuthor(source, target);
        $json.$fromColor(source, target);
        $json.$fromContent(source, target);
        $json.$fromFields(source, target);
        $json.$fromThumbnail(source, target);
        $json.$fromTimestamp(source, target);
        $json.$fromTitle(source, target);
        //Return deserialized  object
        return target;
    }

     public  static  MessageEmbed fromJson( MessageEmbed target,  Path path)throws IOException{

        return fromJson(target, java.nio.file.Files.readString(path));
    }

     public  static  MessageEmbed fromJson( MessageEmbed target,  String content){

        return fromJson(target, new JSONObject(content));
    }

     public  static  JSONObject toJson( MessageEmbed source,  JSONObject target){

        //Create new instance for serializer
        JSONMessageEmbed $json = new JSONMessageEmbed();
        //Serialize attributes
        $json.$toAuthor(source, target);
        $json.$toColor(source, target);
        $json.$toContent(source, target);
        $json.$toFields(source, target);
        $json.$toThumbnail(source, target);
        $json.$toTimestamp(source, target);
        $json.$toTitle(source, target);
        //Return serialized  object
        return target;
    }

}