
package zav.discord.blanc;

import com.google.common.cache.Cache;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Object;
import java.lang.RuntimeException;
import java.lang.String;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import zav.discord.blanc._visitor.ArchitectureVisitor;
import zav.discord.blanc.activity.Activity;
import zav.jra.models.AbstractLink;
import zav.jra.models.AbstractSubreddit;



 abstract  public  class MessageChannelTOP  extends Snowflake  {
     abstract  public  Optional<Message> retrieveMessage( long id);

     abstract  public  Collection<Message> retrieveMessages();

     abstract  public  void send( Message message)throws IOException;

     abstract  public  void send( MessageEmbed messageEmbed)throws IOException;

     abstract  public  void send( AbstractSubreddit subreddit,  AbstractLink link)throws IOException;

     abstract  public  void send( byte[] bytes,  String qualifiedName)throws IOException;

     abstract  public  void send( Role role)throws IOException;

     abstract  public  void send( User user)throws IOException;

     abstract  public  void send( Member member)throws IOException;

     abstract  public  void send( Guild guild)throws IOException;

     abstract  public  void send( Object object)throws IOException;

     abstract  public  void send( BufferedImage image,  String title)throws IOException;

     public  void accept( ArchitectureVisitor visitor){

        visitor.handle(getRealThis());
    }

     abstract  public  MessageChannel getRealThis();

}