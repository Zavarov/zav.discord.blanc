
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



 public  class JSONConfigurationModule   {
     public  static  ConfigurationModule fromJson( ConfigurationModule target,  JSONObject source){

        //Create new instance for deserializer
        JSONConfigurationModule $json = new JSONConfigurationModule();
        //Deserialize attributes
        //Return deserialized  object
        return target;
    }

     public  static  ConfigurationModule fromJson( ConfigurationModule target,  Path path)throws IOException{

        return fromJson(target, java.nio.file.Files.readString(path));
    }

     public  static  ConfigurationModule fromJson( ConfigurationModule target,  String content){

        return fromJson(target, new JSONObject(content));
    }

     public  static  JSONObject toJson( ConfigurationModule source,  JSONObject target){

        //Create new instance for serializer
        JSONConfigurationModule $json = new JSONConfigurationModule();
        //Serialize attributes
        //Return serialized  object
        return target;
    }

}