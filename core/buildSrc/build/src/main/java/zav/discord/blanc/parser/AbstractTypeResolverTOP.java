
package zav.discord.blanc.parser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import zav.discord.blanc.ConfigurationModule;
import zav.discord.blanc.Guild;
import zav.discord.blanc.Member;
import zav.discord.blanc.Message;
import zav.discord.blanc.Role;
import zav.discord.blanc.TextChannel;
import zav.discord.blanc.User;
import zav.discord.blanc.parser._visitor.ParserVisitor;



 abstract  public  class AbstractTypeResolverTOP   {
     abstract  public  ChronoUnit resolveChronoUnit( Argument argument)throws NoSuchElementException;

     abstract  public  String resolveString( Argument argument)throws NoSuchElementException;

     abstract  public  LocalDate resolveLocalDate( Argument argument)throws NoSuchElementException;

     abstract  public  BigDecimal resolveBigDecimal( Argument argument)throws NoSuchElementException;

     abstract  public  Guild resolveGuild( Argument argument)throws NoSuchElementException;

     abstract  public  User resolveUser( Argument argument)throws NoSuchElementException;

     abstract  public  Member resolveMember( Argument argument)throws NoSuchElementException;

     abstract  public  Message resolveMessage( Argument argument)throws NoSuchElementException;

     abstract  public  TextChannel resolveTextChannel( Argument argument)throws NoSuchElementException;

     abstract  public  Role resolveRole( Argument argument)throws NoSuchElementException;

     abstract  public  ConfigurationModule resolveConfigurationModule( Argument argument)throws NoSuchElementException;

     public  void accept( ParserVisitor visitor){

        visitor.handle(getRealThis());
    }

     abstract  public  AbstractTypeResolver getRealThis();

}