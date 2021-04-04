
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



 public  class JSONMessageCommand   {
      protected  final  static  String $AUTHOR  = "$Author";
      protected  final  static  String $MESSAGE  = "$Message";
      protected  final  static  String $MESSAGECHANNEL  = "$MessageChannel";
      protected  final  static  String $SHARD  = "$Shard";
      protected  final  static  String $FLAGS  = "$Flags";
     protected  void $from$Author( JSONObject source,  MessageCommand target){

        throw new UnsupportedOperationException("Deserialization for `$Author` not implemented.");
    }

     protected  void $from$Flags( JSONObject source,  MessageCommand target){

        throw new UnsupportedOperationException("Deserialization for `$Flags` not implemented.");
    }

     protected  void $from$Message( JSONObject source,  MessageCommand target){

        throw new UnsupportedOperationException("Deserialization for `$Message` not implemented.");
    }

     protected  void $from$MessageChannel( JSONObject source,  MessageCommand target){

        throw new UnsupportedOperationException("Deserialization for `$MessageChannel` not implemented.");
    }

     protected  void $from$Shard( JSONObject source,  MessageCommand target){

        throw new UnsupportedOperationException("Deserialization for `$Shard` not implemented.");
    }

     protected  void $to$Author( MessageCommand source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `$Author` not implemented.");
    }

     protected  void $to$Flags( MessageCommand source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `$Flags` not implemented.");
    }

     protected  void $to$Message( MessageCommand source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `$Message` not implemented.");
    }

     protected  void $to$MessageChannel( MessageCommand source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `$MessageChannel` not implemented.");
    }

     protected  void $to$Shard( MessageCommand source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `$Shard` not implemented.");
    }

     public  static  MessageCommand fromJson( MessageCommand target,  JSONObject source){

        //Create new instance for deserializer
        JSONMessageCommand $json = new JSONMessageCommand();
        //Deserialize attributes
        $json.$from$Author(source, target);
        $json.$from$Flags(source, target);
        $json.$from$Message(source, target);
        $json.$from$MessageChannel(source, target);
        $json.$from$Shard(source, target);
        //Return deserialized  object
        return target;
    }

     public  static  MessageCommand fromJson( MessageCommand target,  Path path)throws IOException{

        return fromJson(target, java.nio.file.Files.readString(path));
    }

     public  static  MessageCommand fromJson( MessageCommand target,  String content){

        return fromJson(target, new JSONObject(content));
    }

     public  static  JSONObject toJson( MessageCommand source,  JSONObject target){

        //Create new instance for serializer
        JSONMessageCommand $json = new JSONMessageCommand();
        //Serialize attributes
        $json.$to$Author(source, target);
        $json.$to$Flags(source, target);
        $json.$to$Message(source, target);
        $json.$to$MessageChannel(source, target);
        $json.$to$Shard(source, target);
        //Return serialized  object
        return target;
    }

}