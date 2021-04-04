
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



 public  class MessageEmbedFactory   {
     public  static  MessageEmbed create(){

        return create(() -> new MessageEmbed());
    }

     public  static  MessageEmbed create( Supplier<? extends MessageEmbed> _factoryMessageEmbed){

        MessageEmbed _MessageEmbedInstance = _factoryMessageEmbed.get();
        return _MessageEmbedInstance;
    }

     public  static  MessageEmbed create( Optional<Color> color,  Optional<String> thumbnail,  Optional<Title> title,  Optional<String> content,  Optional<Instant> timestamp,  Optional<Author> author,  List<Field> fields){

        return create(() -> new MessageEmbed(),color,thumbnail,title,content,timestamp,author,fields);
    }

     public  static  MessageEmbed create( Supplier<? extends MessageEmbed> _factoryMessageEmbed,  Optional<Color> color,  Optional<String> thumbnail,  Optional<Title> title,  Optional<String> content,  Optional<Instant> timestamp,  Optional<Author> author,  List<Field> fields){

        MessageEmbed _MessageEmbedInstance = _factoryMessageEmbed.get();
        _MessageEmbedInstance.setColor(color);
        _MessageEmbedInstance.setThumbnail(thumbnail);
        _MessageEmbedInstance.setTitle(title);
        _MessageEmbedInstance.setContent(content);
        _MessageEmbedInstance.setTimestamp(timestamp);
        _MessageEmbedInstance.setAuthor(author);
        _MessageEmbedInstance.setFields(fields);
        return _MessageEmbedInstance;
    }

}