
package zav.discord.blanc.command._json;

import java.io.IOException;
import java.io.Serializable;
import java.lang.CharSequence;
import java.lang.Class;
import java.lang.Comparable;
import java.lang.InterruptedException;
import java.lang.Object;
import java.lang.Runnable;
import java.lang.String;
import java.lang.StringBuffer;
import java.lang.Throwable;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.json.JSONObject;
import zav.discord.blanc.Guild;
import zav.discord.blanc.Member;
import zav.discord.blanc.Message;
import zav.discord.blanc.MessageChannel;
import zav.discord.blanc.MessageEmbed;
import zav.discord.blanc.Permission;
import zav.discord.blanc.Role;
import zav.discord.blanc.SelfUser;
import zav.discord.blanc.Shard;
import zav.discord.blanc.TextChannel;
import zav.discord.blanc.User;
import zav.discord.blanc.command.*;
import zav.discord.blanc.command._visitor.CommandVisitor;
import zav.discord.blanc.parser.AbstractTypeResolver;
import zav.discord.blanc.parser.Argument;



 public  class JSONCommandBuilder   {
      protected  final  static  String SHARD  = "shard";
      protected  final  static  String TYPERESOLVER  = "typeResolver";
     protected  void $fromShard( JSONObject source,  CommandBuilder target){

        throw new UnsupportedOperationException("Deserialization for `shard` not implemented.");
    }

     protected  void $fromTypeResolver( JSONObject source,  CommandBuilder target){

        throw new UnsupportedOperationException("Deserialization for `typeResolver` not implemented.");
    }

     protected  void $toShard( CommandBuilder source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `shard` not implemented.");
    }

     protected  void $toTypeResolver( CommandBuilder source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `typeResolver` not implemented.");
    }

     public  static  CommandBuilder fromJson( CommandBuilder target,  JSONObject source){

        //Create new instance for deserializer
        JSONCommandBuilder $json = new JSONCommandBuilder();
        //Deserialize attributes
        $json.$fromShard(source, target);
        $json.$fromTypeResolver(source, target);
        //Return deserialized  object
        return target;
    }

     public  static  CommandBuilder fromJson( CommandBuilder target,  Path path)throws IOException{

        return fromJson(target, java.nio.file.Files.readString(path));
    }

     public  static  CommandBuilder fromJson( CommandBuilder target,  String content){

        return fromJson(target, new JSONObject(content));
    }

     public  static  JSONObject toJson( CommandBuilder source,  JSONObject target){

        //Create new instance for serializer
        JSONCommandBuilder $json = new JSONCommandBuilder();
        //Serialize attributes
        $json.$toShard(source, target);
        $json.$toTypeResolver(source, target);
        //Return serialized  object
        return target;
    }

}