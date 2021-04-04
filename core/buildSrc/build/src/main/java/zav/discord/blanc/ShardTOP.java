
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



 abstract  public  class ShardTOP   implements Killable {
      private  int id ;
     abstract  public  SelfUser retrieveSelfUser();

     abstract  public  Optional<User> retrieveUser( long id);

     abstract  public  Collection<User> retrieveUsers();

     abstract  public  Optional<Guild> retrieveGuild( long id);

     abstract  public  Collection<Guild> retrieveGuilds();

     public  void accept( ArchitectureVisitor visitor){

        visitor.handle(getRealThis());
    }

     abstract  public  Shard getRealThis();

     public  void setId( int id){
        this.id = id;
    }

     public  int getId(){
        return this.id;
    }

}