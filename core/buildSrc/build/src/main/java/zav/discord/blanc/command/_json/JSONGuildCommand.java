
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



 public  class JSONGuildCommand   {
      protected  final  static  String $GUILD  = "$Guild";
      protected  final  static  String $MESSAGE  = "$Message";
      protected  final  static  String $TEXTCHANNEL  = "$TextChannel";
      protected  final  static  String $FLAGS  = "$Flags";
      protected  final  static  String $SHARD  = "$Shard";
      protected  final  static  String $AUTHOR  = "$Author";
     protected  void $from$Author( JSONObject source,  GuildCommand target){

        throw new UnsupportedOperationException("Deserialization for `$Author` not implemented.");
    }

     protected  void $from$Flags( JSONObject source,  GuildCommand target){

        throw new UnsupportedOperationException("Deserialization for `$Flags` not implemented.");
    }

     protected  void $from$Guild( JSONObject source,  GuildCommand target){

        throw new UnsupportedOperationException("Deserialization for `$Guild` not implemented.");
    }

     protected  void $from$Message( JSONObject source,  GuildCommand target){

        throw new UnsupportedOperationException("Deserialization for `$Message` not implemented.");
    }

     protected  void $from$Shard( JSONObject source,  GuildCommand target){

        throw new UnsupportedOperationException("Deserialization for `$Shard` not implemented.");
    }

     protected  void $from$TextChannel( JSONObject source,  GuildCommand target){

        throw new UnsupportedOperationException("Deserialization for `$TextChannel` not implemented.");
    }

     protected  void $to$Author( GuildCommand source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `$Author` not implemented.");
    }

     protected  void $to$Flags( GuildCommand source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `$Flags` not implemented.");
    }

     protected  void $to$Guild( GuildCommand source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `$Guild` not implemented.");
    }

     protected  void $to$Message( GuildCommand source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `$Message` not implemented.");
    }

     protected  void $to$Shard( GuildCommand source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `$Shard` not implemented.");
    }

     protected  void $to$TextChannel( GuildCommand source,  JSONObject target){

        throw new UnsupportedOperationException("Serialization for `$TextChannel` not implemented.");
    }

     public  static  GuildCommand fromJson( GuildCommand target,  JSONObject source){

        //Create new instance for deserializer
        JSONGuildCommand $json = new JSONGuildCommand();
        //Deserialize attributes
        $json.$from$Author(source, target);
        $json.$from$Flags(source, target);
        $json.$from$Guild(source, target);
        $json.$from$Message(source, target);
        $json.$from$Shard(source, target);
        $json.$from$TextChannel(source, target);
        //Return deserialized  object
        return target;
    }

     public  static  GuildCommand fromJson( GuildCommand target,  Path path)throws IOException{

        return fromJson(target, java.nio.file.Files.readString(path));
    }

     public  static  GuildCommand fromJson( GuildCommand target,  String content){

        return fromJson(target, new JSONObject(content));
    }

     public  static  JSONObject toJson( GuildCommand source,  JSONObject target){

        //Create new instance for serializer
        JSONGuildCommand $json = new JSONGuildCommand();
        //Serialize attributes
        $json.$to$Author(source, target);
        $json.$to$Flags(source, target);
        $json.$to$Guild(source, target);
        $json.$to$Message(source, target);
        $json.$to$Shard(source, target);
        $json.$to$TextChannel(source, target);
        //Return serialized  object
        return target;
    }

}