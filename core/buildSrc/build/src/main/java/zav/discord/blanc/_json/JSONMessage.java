
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



 public  class JSONMessage   {
      protected  final  static  String ATTACHMENTS  = "attachments";
      protected  final  static  String MESSAGEEMBEDS  = "messageEmbeds";
      protected  final  static  String AUTHOR  = "author";
      protected  final  static  String CONTENT  = "content";
      protected  final  static  String ID  = "id";
      protected  final  static  String CREATED  = "created";
     protected  void $fromAttachments( JSONObject source,  Message target){

        throw new UnsupportedOperationException("Deserialization for `attachments` not implemented.");
    }

     protected  void $fromAuthor( JSONObject source,  Message target){

        throw new UnsupportedOperationException("Deserialization for `author` not implemented.");
    }

     protected  void $fromContent( JSONObject source,  Message target){

        target.setContent(source.optString("content", "").isBlank() ? Optional.empty() : Optional.of(source.getString("content")));

    }

     protected  void $fromCreated( JSONObject source,  Message target){

        throw new UnsupportedOperationException("Deserialization for `created` not implemented.");
    }

     protected  void $fromId( JSONObject source,  Message target){

        target.setId(source.getLong("id"));

    }

     protected  void $fromMessageEmbeds( JSONObject source,  Message target){

        throw new UnsupportedOperationException("Deserialization for `messageEmbeds` not implemented.");
    }

     protected  void $toAttachments( Message source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `attachments` not implemented.");
    }

     protected  void $toAuthor( Message source,  JSONObject target){

        target.put("author", JSONUser.toJson(source.getAuthor(), new JSONObject()));

    }

     protected  void $toContent( Message source,  JSONObject target){

        source.ifPresentContent(
            $value -> target.put("content", $value)
        );

    }

     protected  void $toCreated( Message source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `created` not implemented.");
    }

     protected  void $toId( Message source,  JSONObject target){

        target.put("id", source.getId());

    }

     protected  void $toMessageEmbeds( Message source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `messageEmbeds` not implemented.");
    }

     public  static  Message fromJson( Message target,  JSONObject source){

        //Create new instance for deserializer
        JSONMessage $json = new JSONMessage();
        //Deserialize attributes
        $json.$fromAttachments(source, target);
        $json.$fromAuthor(source, target);
        $json.$fromContent(source, target);
        $json.$fromCreated(source, target);
        $json.$fromId(source, target);
        $json.$fromMessageEmbeds(source, target);
        //Return deserialized  object
        return target;
    }

     public  static  Message fromJson( Message target,  Path path)throws IOException{

        return fromJson(target, java.nio.file.Files.readString(path));
    }

     public  static  Message fromJson( Message target,  String content){

        return fromJson(target, new JSONObject(content));
    }

     public  static  JSONObject toJson( Message source,  JSONObject target){

        //Create new instance for serializer
        JSONMessage $json = new JSONMessage();
        //Serialize attributes
        $json.$toAttachments(source, target);
        $json.$toAuthor(source, target);
        $json.$toContent(source, target);
        $json.$toCreated(source, target);
        $json.$toId(source, target);
        $json.$toMessageEmbeds(source, target);
        //Return serialized  object
        return target;
    }

}