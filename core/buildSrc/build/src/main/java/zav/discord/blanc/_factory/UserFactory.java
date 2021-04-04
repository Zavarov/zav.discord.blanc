
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



 public  class UserFactory   {
     public  static  User create( Supplier<? extends User> _factoryUser,  OnlineStatus onlineStatus,  long id,  String name){

        User _UserInstance = _factoryUser.get();
        _UserInstance.setOnlineStatus(onlineStatus);
        _UserInstance.setId(id);
        _UserInstance.setName(name);
        return _UserInstance;
    }

     public  static  User create( Supplier<? extends User> _factoryUser,  OnlineStatus onlineStatus,  Collection<Rank> ranks,  long id,  String name){

        User _UserInstance = _factoryUser.get();
        _UserInstance.setOnlineStatus(onlineStatus);
        _UserInstance.setRanks(ranks);
        _UserInstance.setId(id);
        _UserInstance.setName(name);
        return _UserInstance;
    }

}