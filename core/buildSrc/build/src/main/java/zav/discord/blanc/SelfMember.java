
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



 abstract  public  class SelfMember  extends Member  {
     abstract  public  void modifyStatusMessage( String statusMessage);

     abstract  public  void modifyAvatar( InputStream avatar);

     abstract  public  void modifyNickname( String nickname);

     public  void accept( ArchitectureVisitor visitor){

        visitor.handle(getRealThis());
    }

     public  SelfMember getRealThis(){
        return this;
    }

}