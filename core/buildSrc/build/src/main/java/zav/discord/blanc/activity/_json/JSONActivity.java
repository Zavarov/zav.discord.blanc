
package zav.discord.blanc.activity._json;

import com.google.common.cache.Cache;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.lang.CharSequence;
import java.lang.Class;
import java.lang.Comparable;
import java.lang.Double;
import java.lang.InterruptedException;
import java.lang.Object;
import java.lang.String;
import java.lang.StringBuffer;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.json.JSONObject;
import zav.discord.blanc.Guild;
import zav.discord.blanc.TextChannel;
import zav.discord.blanc.activity.*;
import zav.discord.blanc.activity._visitor.ActivityVisitor;



 public  class JSONActivity   {
      protected  final  static  String ACTIVITY  = "activity";
     protected  void $fromActivity( JSONObject source,  Activity target){

        throw new UnsupportedOperationException("Deserialization for `activity` not implemented.");
    }

     protected  void $toActivity( Activity source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `activity` not implemented.");
    }

     public  static  Activity fromJson( Activity target,  JSONObject source){

        //Create new instance for deserializer
        JSONActivity $json = new JSONActivity();
        //Deserialize attributes
        $json.$fromActivity(source, target);
        //Return deserialized  object
        return target;
    }

     public  static  Activity fromJson( Activity target,  Path path)throws IOException{

        return fromJson(target, java.nio.file.Files.readString(path));
    }

     public  static  Activity fromJson( Activity target,  String content){

        return fromJson(target, new JSONObject(content));
    }

     public  static  JSONObject toJson( Activity source,  JSONObject target){

        //Create new instance for serializer
        JSONActivity $json = new JSONActivity();
        //Serialize attributes
        $json.$toActivity(source, target);
        //Return serialized  object
        return target;
    }

}