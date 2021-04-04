
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



 public  class FieldFactory   {
     public  static  Field create( String title,  Object content,  boolean inline){

        return create(() -> new Field(),title,content,inline);
    }

     public  static  Field create( Supplier<? extends Field> _factoryField,  String title,  Object content,  boolean inline){

        Field _FieldInstance = _factoryField.get();
        _FieldInstance.setTitle(title);
        _FieldInstance.setContent(content);
        _FieldInstance.setInline(inline);
        return _FieldInstance;
    }

}