
package zav.discord.blanc._json;

import com.google.common.cache.Cache;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Object;
import java.lang.RuntimeException;
import java.lang.String;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.json.JSONObject;
import zav.discord.blanc.*;
import zav.discord.blanc._visitor.ArchitectureVisitor;
import zav.discord.blanc.activity.Activity;
import zav.jra.models.AbstractLink;
import zav.jra.models.AbstractSubreddit;



 public  class JSONAttachment   {
     public  static  Attachment fromJson( Attachment target,  JSONObject source){

        //Create new instance for deserializer
        JSONAttachment $json = new JSONAttachment();
        //Deserialize attributes
        //Return deserialized  object
        return target;
    }

     public  static  Attachment fromJson( Attachment target,  Path path)throws IOException{

        return fromJson(target, java.nio.file.Files.readString(path));
    }

     public  static  Attachment fromJson( Attachment target,  String content){

        return fromJson(target, new JSONObject(content));
    }

     public  static  JSONObject toJson( Attachment source,  JSONObject target){

        //Create new instance for serializer
        JSONAttachment $json = new JSONAttachment();
        //Serialize attributes
        //Return serialized  object
        return target;
    }

}