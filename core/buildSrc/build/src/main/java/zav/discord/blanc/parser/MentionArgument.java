
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



 public  interface MentionArgument extends Argument {
     abstract  Number getNumber();
    default  public  void accept( ParserVisitor visitor){

        visitor.handle(getRealThis());
    }
    default  public  MentionArgument getRealThis(){
        return this;
    }
}