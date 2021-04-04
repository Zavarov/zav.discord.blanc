
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



 abstract  public  class Member  extends User  {
     abstract  public  Optional<String> retrieveNickname();

     abstract  public  Collection<Role> retrieveRoles();

     abstract  public  void modifyRoles( Collection<Role> rolesToAdd,  Collection<Role> rolesToRemove);

     abstract  public  Set<Permission> getPermissions( TextChannel textChannel);

     public  void accept( ArchitectureVisitor visitor){

        visitor.handle(getRealThis());
    }

     public  Member getRealThis(){
        return this;
    }

}