
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
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
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
import zav.discord.blanc.parser.ArithmeticArgument;
import zav.discord.blanc.parser.IntermediateCommand;
import zav.discord.blanc.parser.MentionArgument;
import zav.discord.blanc.parser.Parser;
import zav.discord.blanc.parser.StringArgument;
import zav.jra.models.AbstractLink;
import zav.jra.models.AbstractSubreddit;



 abstract  public  class CommandBuilderTOP   {
      protected  AbstractTypeResolver typeResolver ;
      protected  Shard shard ;
    
 public  CommandBuilderTOP( BiFunction<? super Guild,? super TextChannel,? extends AbstractTypeResolver> typeResolverFunction,  Shard shard){
    }
     abstract  protected  Optional<Command> build( String name,  List<? extends Argument> arguments,  List<String> flags);

     abstract  public  Optional<Command> build( Message message,  MessageChannel channel);

     abstract  public  Optional<Command> build( Message message,  Guild guild,  TextChannel textChannel);

     public  void accept( CommandVisitor visitor){

        visitor.handle(getRealThis());
    }

     abstract  public  CommandBuilder getRealThis();

     public  Optional<Guild> retrieveGuildShard( long id){
        return this.shard.retrieveGuild(id);
    }

     public  Optional<User> retrieveUserShard( long id){
        return this.shard.retrieveUser(id);
    }

     public  Collection<User> retrieveUsersShard(){
        return this.shard.retrieveUsers();
    }

     public  ConfigurationModule resolveConfigurationModuleTypeResolver( Argument argument)throws NoSuchElementException{
        return this.typeResolver.resolveConfigurationModule(argument);
    }

     public  ChronoUnit resolveChronoUnitTypeResolver( Argument argument)throws NoSuchElementException{
        return this.typeResolver.resolveChronoUnit(argument);
    }

     public  Collection<Guild> retrieveGuildsShard(){
        return this.shard.retrieveGuilds();
    }

     public  Guild resolveGuildTypeResolver( Argument argument)throws NoSuchElementException{
        return this.typeResolver.resolveGuild(argument);
    }

     public  String resolveStringTypeResolver( Argument argument)throws NoSuchElementException{
        return this.typeResolver.resolveString(argument);
    }

     public  void shutdownShard(){
        this.shard.shutdown();
    }

     public  AbstractTypeResolver getTypeResolver(){
        return this.typeResolver;
    }

     public  Message resolveMessageTypeResolver( Argument argument)throws NoSuchElementException{
        return this.typeResolver.resolveMessage(argument);
    }

     public  void setTypeResolver( AbstractTypeResolver typeResolver){
        this.typeResolver = typeResolver;
    }

     public  Member resolveMemberTypeResolver( Argument argument)throws NoSuchElementException{
        return this.typeResolver.resolveMember(argument);
    }

     public  Role resolveRoleTypeResolver( Argument argument)throws NoSuchElementException{
        return this.typeResolver.resolveRole(argument);
    }

     public  Shard getShard(){
        return this.shard;
    }

     public  LocalDate resolveLocalDateTypeResolver( Argument argument)throws NoSuchElementException{
        return this.typeResolver.resolveLocalDate(argument);
    }

     public  void setShard( Shard shard){
        this.shard = shard;
    }

     public  TextChannel resolveTextChannelTypeResolver( Argument argument)throws NoSuchElementException{
        return this.typeResolver.resolveTextChannel(argument);
    }

     public  SelfUser retrieveSelfUserShard(){
        return this.shard.retrieveSelfUser();
    }

     public  User resolveUserTypeResolver( Argument argument)throws NoSuchElementException{
        return this.typeResolver.resolveUser(argument);
    }

     public  BigDecimal resolveBigDecimalTypeResolver( Argument argument)throws NoSuchElementException{
        return this.typeResolver.resolveBigDecimal(argument);
    }

}