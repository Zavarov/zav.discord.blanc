
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



 public  class TextChannelFactory   {
     public  static  TextChannel create( Supplier<? extends TextChannel> _factoryTextChannel,  long id,  String name){

        TextChannel _TextChannelInstance = _factoryTextChannel.get();
        _TextChannelInstance.setId(id);
        _TextChannelInstance.setName(name);
        return _TextChannelInstance;
    }

     public  static  TextChannel create( Supplier<? extends TextChannel> _factoryTextChannel,  Set<String> subreddits,  long id,  String name){

        TextChannel _TextChannelInstance = _factoryTextChannel.get();
        _TextChannelInstance.setSubreddits(subreddits);
        _TextChannelInstance.setId(id);
        _TextChannelInstance.setName(name);
        return _TextChannelInstance;
    }

}