
package zav.discord.blanc.command;

import com.google.common.cache.Cache;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Class;
import java.lang.InterruptedException;
import java.lang.Object;
import java.lang.Runnable;
import java.lang.RuntimeException;
import java.lang.String;
import java.lang.Throwable;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import zav.discord.blanc.Attachment;
import zav.discord.blanc.Author;
import zav.discord.blanc.Client;
import zav.discord.blanc.ConfigurationModule;
import zav.discord.blanc.Field;
import zav.discord.blanc.Guild;
import zav.discord.blanc.Killable;
import zav.discord.blanc.Member;
import zav.discord.blanc.Message;
import zav.discord.blanc.MessageChannel;
import zav.discord.blanc.MessageEmbed;
import zav.discord.blanc.OnlineStatus;
import zav.discord.blanc.Permission;
import zav.discord.blanc.Printable;
import zav.discord.blanc.PrivateChannel;
import zav.discord.blanc.Rank;
import zav.discord.blanc.Role;
import zav.discord.blanc.SelfMember;
import zav.discord.blanc.SelfUser;
import zav.discord.blanc.Shard;
import zav.discord.blanc.Snowflake;
import zav.discord.blanc.TextChannel;
import zav.discord.blanc.Title;
import zav.discord.blanc.User;
import zav.discord.blanc.Webhook;
import zav.discord.blanc.activity.Activity;
import zav.discord.blanc.command._visitor.CommandVisitor;
import zav.discord.blanc.parser.AbstractTypeResolver;
import zav.discord.blanc.parser.Argument;
import zav.jra.models.AbstractLink;
import zav.jra.models.AbstractSubreddit;



 abstract  public  class MessageCommand  extends Command  {
      private  User $Author ;
      private  MessageChannel $MessageChannel ;
     public  void accept( CommandVisitor visitor){

        visitor.handle(getRealThis());
    }

     public  MessageCommand getRealThis(){
        return this;
    }

     public  void send$MessageChannel( byte[] bytes,  String qualifiedName)throws IOException{
        this.$MessageChannel.send(bytes,qualifiedName);
    }

     public  User get$Author(){
        return this.$Author;
    }

     public  void send$MessageChannel( Role role)throws IOException{
        this.$MessageChannel.send(role);
    }

     public  void send$MessageChannel( Message message)throws IOException{
        this.$MessageChannel.send(message);
    }

     public  String getAsMention$Author(){
        return this.$Author.getAsMention();
    }

     public  Collection<Message> retrieveMessages$MessageChannel(){
        return this.$MessageChannel.retrieveMessages();
    }

     public  Optional<Message> retrieveMessage$MessageChannel( long id){
        return this.$MessageChannel.retrieveMessage(id);
    }

     public  MessageChannel get$MessageChannel(){
        return this.$MessageChannel;
    }

     public  PrivateChannel retrievePrivateChannel$Author(){
        return this.$Author.retrievePrivateChannel();
    }

     public  void send$MessageChannel( Guild guild)throws IOException{
        this.$MessageChannel.send(guild);
    }

     public  void send$MessageChannel( Member member)throws IOException{
        this.$MessageChannel.send(member);
    }

     public  void send$MessageChannel( MessageEmbed messageEmbed)throws IOException{
        this.$MessageChannel.send(messageEmbed);
    }

     public  void set$Author( User $Author){
        this.$Author = $Author;
    }

     public  MessageEmbed toMessageEmbed$Author(){
        return this.$Author.toMessageEmbed();
    }

     public  void send$MessageChannel( Object object)throws IOException{
        this.$MessageChannel.send(object);
    }

     public  void set$MessageChannel( MessageChannel $MessageChannel){
        this.$MessageChannel = $MessageChannel;
    }

     public  void send$MessageChannel( AbstractSubreddit subreddit,  AbstractLink link)throws IOException{
        this.$MessageChannel.send(subreddit,link);
    }

     public  void send$MessageChannel( BufferedImage image,  String title)throws IOException{
        this.$MessageChannel.send(image,title);
    }

     public  void send$MessageChannel( User user)throws IOException{
        this.$MessageChannel.send(user);
    }

}