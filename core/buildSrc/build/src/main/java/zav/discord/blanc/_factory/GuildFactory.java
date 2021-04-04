
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



 public  class GuildFactory   {
     public  static  Guild create( Supplier<? extends Guild> _factoryGuild,  Activity activity,  long id,  String name){

        Guild _GuildInstance = _factoryGuild.get();
        _GuildInstance.setActivity(activity);
        _GuildInstance.setId(id);
        _GuildInstance.setName(name);
        return _GuildInstance;
    }

     public  static  Guild create( Supplier<? extends Guild> _factoryGuild,  Optional<String> prefix,  List<String> blacklist,  Activity activity,  long id,  String name){

        Guild _GuildInstance = _factoryGuild.get();
        _GuildInstance.setPrefix(prefix);
        _GuildInstance.setBlacklist(blacklist);
        _GuildInstance.setActivity(activity);
        _GuildInstance.setId(id);
        _GuildInstance.setName(name);
        return _GuildInstance;
    }

}