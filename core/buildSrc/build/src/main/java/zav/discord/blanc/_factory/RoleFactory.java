
package zav.discord.blanc._factory;

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
import java.util.function.Supplier;
import zav.discord.blanc.*;
import zav.discord.blanc.activity.Activity;
import zav.jra.models.AbstractLink;
import zav.jra.models.AbstractSubreddit;



 public  class RoleFactory   {
     public  static  Role create( Supplier<? extends Role> _factoryRole,  long id,  String name){

        Role _RoleInstance = _factoryRole.get();
        _RoleInstance.setId(id);
        _RoleInstance.setName(name);
        return _RoleInstance;
    }

     public  static  Role create( Supplier<? extends Role> _factoryRole,  Optional<String> group,  long id,  String name){

        Role _RoleInstance = _factoryRole.get();
        _RoleInstance.setGroup(group);
        _RoleInstance.setId(id);
        _RoleInstance.setName(name);
        return _RoleInstance;
    }

}