
package zav.discord.blanc.parser._json;

import java.io.IOException;
import java.lang.String;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.json.JSONObject;
import zav.discord.blanc.ConfigurationModule;
import zav.discord.blanc.Guild;
import zav.discord.blanc.Member;
import zav.discord.blanc.Message;
import zav.discord.blanc.Role;
import zav.discord.blanc.TextChannel;
import zav.discord.blanc.User;
import zav.discord.blanc.parser.*;
import zav.discord.blanc.parser._visitor.ParserVisitor;



 public  class JSONAbstractTypeResolver   {
     public  static  AbstractTypeResolver fromJson( AbstractTypeResolver target,  JSONObject source){

        //Create new instance for deserializer
        JSONAbstractTypeResolver $json = new JSONAbstractTypeResolver();
        //Deserialize attributes
        //Return deserialized  object
        return target;
    }

     public  static  AbstractTypeResolver fromJson( AbstractTypeResolver target,  Path path)throws IOException{

        return fromJson(target, java.nio.file.Files.readString(path));
    }

     public  static  AbstractTypeResolver fromJson( AbstractTypeResolver target,  String content){

        return fromJson(target, new JSONObject(content));
    }

     public  static  JSONObject toJson( AbstractTypeResolver source,  JSONObject target){

        //Create new instance for serializer
        JSONAbstractTypeResolver $json = new JSONAbstractTypeResolver();
        //Serialize attributes
        //Return serialized  object
        return target;
    }

}