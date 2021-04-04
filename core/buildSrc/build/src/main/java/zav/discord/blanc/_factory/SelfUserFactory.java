
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



 public  class SelfUserFactory   {
     public  static  SelfUser create( Supplier<? extends SelfUser> _factorySelfUser,  OnlineStatus onlineStatus,  long id,  String name){

        SelfUser _SelfUserInstance = _factorySelfUser.get();
        _SelfUserInstance.setOnlineStatus(onlineStatus);
        _SelfUserInstance.setId(id);
        _SelfUserInstance.setName(name);
        return _SelfUserInstance;
    }

     public  static  SelfUser create( Supplier<? extends SelfUser> _factorySelfUser,  OnlineStatus onlineStatus,  Collection<Rank> ranks,  long id,  String name){

        SelfUser _SelfUserInstance = _factorySelfUser.get();
        _SelfUserInstance.setOnlineStatus(onlineStatus);
        _SelfUserInstance.setRanks(ranks);
        _SelfUserInstance.setId(id);
        _SelfUserInstance.setName(name);
        return _SelfUserInstance;
    }

}