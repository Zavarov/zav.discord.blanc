
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



 public  class TitleFactory   {
     public  static  Title create( String name){

        return create(() -> new Title(),name);
    }

     public  static  Title create( String name,  Optional<String> url){

        return create(() -> new Title(),name,url);
    }

     public  static  Title create( Supplier<? extends Title> _factoryTitle,  String name){

        Title _TitleInstance = _factoryTitle.get();
        _TitleInstance.setName(name);
        return _TitleInstance;
    }

     public  static  Title create( Supplier<? extends Title> _factoryTitle,  String name,  Optional<String> url){

        Title _TitleInstance = _factoryTitle.get();
        _TitleInstance.setName(name);
        _TitleInstance.setUrl(url);
        return _TitleInstance;
    }

}