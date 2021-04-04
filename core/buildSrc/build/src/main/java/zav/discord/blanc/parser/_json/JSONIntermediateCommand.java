
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



 public  class JSONIntermediateCommand   {
     public  static  IntermediateCommand fromJson( IntermediateCommand target,  JSONObject source){

        //Create new instance for deserializer
        JSONIntermediateCommand $json = new JSONIntermediateCommand();
        //Deserialize attributes
        //Return deserialized  object
        return target;
    }

     public  static  IntermediateCommand fromJson( IntermediateCommand target,  Path path)throws IOException{

        return fromJson(target, java.nio.file.Files.readString(path));
    }

     public  static  IntermediateCommand fromJson( IntermediateCommand target,  String content){

        return fromJson(target, new JSONObject(content));
    }

     public  static  JSONObject toJson( IntermediateCommand source,  JSONObject target){

        //Create new instance for serializer
        JSONIntermediateCommand $json = new JSONIntermediateCommand();
        //Serialize attributes
        //Return serialized  object
        return target;
    }

}