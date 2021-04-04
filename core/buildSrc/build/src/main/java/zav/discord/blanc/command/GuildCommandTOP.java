
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



 abstract  public  class GuildCommandTOP  extends Command  {
      private  Member $Author ;
      private  TextChannel $TextChannel ;
      private  Guild $Guild ;
     public  void accept( CommandVisitor visitor){

        visitor.handle(getRealThis());
    }

     abstract  public  GuildCommand getRealThis();

     public  Collection<Member> retrieveMembers$Guild(){
        return this.$Guild.retrieveMembers();
    }

     public  void set$Guild( Guild $Guild){
        this.$Guild = $Guild;
    }

     public  Collection<Role> retrieveRoles$Guild(){
        return this.$Guild.retrieveRoles();
    }

     public  String getAsMention$TextChannel(){
        return this.$TextChannel.getAsMention();
    }

     public  void send$TextChannel( Member member)throws IOException{
        this.$TextChannel.send(member);
    }

     public  void send$TextChannel( AbstractSubreddit subreddit,  AbstractLink link)throws IOException{
        this.$TextChannel.send(subreddit,link);
    }

     public  SelfMember retrieveSelfMember$Guild(){
        return this.$Guild.retrieveSelfMember();
    }

     public  Collection<Webhook> retrieveWebhooks$TextChannel( String name){
        return this.$TextChannel.retrieveWebhooks(name);
    }

     public  void send$TextChannel( Object object)throws IOException{
        this.$TextChannel.send(object);
    }

     public  void leave$Guild(){
        this.$Guild.leave();
    }

     public  PrivateChannel retrievePrivateChannel$Author(){
        return this.$Author.retrievePrivateChannel();
    }

     public  Collection<TextChannel> retrieveTextChannels$Guild(){
        return this.$Guild.retrieveTextChannels();
    }

     public  Set<Permission> getPermissions$Author( TextChannel textChannel){
        return this.$Author.getPermissions(textChannel);
    }

     public  void set$Author( Member $Author){
        this.$Author = $Author;
    }

     public  Member get$Author(){
        return this.$Author;
    }

     public  void send$TextChannel( Message message)throws IOException{
        this.$TextChannel.send(message);
    }

     public  String getAsMention$Author(){
        return this.$Author.getAsMention();
    }

     public  Guild get$Guild(){
        return this.$Guild;
    }

     public  Collection<Webhook> retrieveWebhooks$TextChannel(){
        return this.$TextChannel.retrieveWebhooks();
    }

     public  Optional<String> retrieveNickname$Author(){
        return this.$Author.retrieveNickname();
    }

     public  void send$TextChannel( Role role)throws IOException{
        this.$TextChannel.send(role);
    }

     public  Optional<Role> retrieveRole$Guild( long id){
        return this.$Guild.retrieveRole(id);
    }

     public  void send$TextChannel( Guild guild)throws IOException{
        this.$TextChannel.send(guild);
    }

     public  Optional<Message> retrieveMessage$TextChannel( long id){
        return this.$TextChannel.retrieveMessage(id);
    }

     public  Optional<TextChannel> retrieveTextChannel$Guild( long id){
        return this.$Guild.retrieveTextChannel(id);
    }

     public  MessageEmbed toMessageEmbed$Guild(){
        return this.$Guild.toMessageEmbed();
    }

     public  void send$TextChannel( User user)throws IOException{
        this.$TextChannel.send(user);
    }

     public  void send$TextChannel( MessageEmbed messageEmbed)throws IOException{
        this.$TextChannel.send(messageEmbed);
    }

     public  MessageEmbed toMessageEmbed$Author(){
        return this.$Author.toMessageEmbed();
    }

     public  Collection<Role> retrieveRoles$Author(){
        return this.$Author.retrieveRoles();
    }

     public  void set$TextChannel( TextChannel $TextChannel){
        this.$TextChannel = $TextChannel;
    }

     public  TextChannel get$TextChannel(){
        return this.$TextChannel;
    }

     public  void send$TextChannel( BufferedImage image,  String title)throws IOException{
        this.$TextChannel.send(image,title);
    }

     public  void modifyRoles$Author( Collection<Role> rolesToAdd,  Collection<Role> rolesToRemove){
        this.$Author.modifyRoles(rolesToAdd,rolesToRemove);
    }

     public  void send$TextChannel( byte[] bytes,  String qualifiedName)throws IOException{
        this.$TextChannel.send(bytes,qualifiedName);
    }

     public  Webhook createWebhook$TextChannel( String name){
        return this.$TextChannel.createWebhook(name);
    }

     public  Collection<Message> retrieveMessages$TextChannel(){
        return this.$TextChannel.retrieveMessages();
    }

     public  Optional<Member> retrieveMember$Guild( long id){
        return this.$Guild.retrieveMember(id);
    }

}