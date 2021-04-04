
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



 public  class JSONPrintable   {
     public  static  Printable fromJson( Printable target,  JSONObject source){

        //Create new instance for deserializer
        JSONPrintable $json = new JSONPrintable();
        //Deserialize attributes
        //Return deserialized  object
        return target;
    }

     public  static  Printable fromJson( Printable target,  Path path)throws IOException{

        return fromJson(target, java.nio.file.Files.readString(path));
    }

     public  static  Printable fromJson( Printable target,  String content){

        return fromJson(target, new JSONObject(content));
    }

     public  static  JSONObject toJson( Printable source,  JSONObject target){

        //Create new instance for serializer
        JSONPrintable $json = new JSONPrintable();
        //Serialize attributes
        //Return serialized  object
        return target;
    }

}