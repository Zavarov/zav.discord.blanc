
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



 public  class AuthorFactory   {
     public  static  Author create( String name){

        return create(() -> new Author(),name);
    }

     public  static  Author create( String name,  Optional<String> url){

        return create(() -> new Author(),name,url);
    }

     public  static  Author create( Supplier<? extends Author> _factoryAuthor,  String name){

        Author _AuthorInstance = _factoryAuthor.get();
        _AuthorInstance.setName(name);
        return _AuthorInstance;
    }

     public  static  Author create( Supplier<? extends Author> _factoryAuthor,  String name,  Optional<String> url){

        Author _AuthorInstance = _factoryAuthor.get();
        _AuthorInstance.setName(name);
        _AuthorInstance.setUrl(url);
        return _AuthorInstance;
    }

}