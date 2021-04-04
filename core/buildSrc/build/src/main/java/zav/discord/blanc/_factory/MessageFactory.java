
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



 public  class MessageFactory   {
     public  static  Message create( long id,  Instant created,  User author){

        return create(() -> new Message(),id,created,author);
    }

     public  static  Message create( Supplier<? extends Message> _factoryMessage,  long id,  Instant created,  User author){

        Message _MessageInstance = _factoryMessage.get();
        _MessageInstance.setId(id);
        _MessageInstance.setCreated(created);
        _MessageInstance.setAuthor(author);
        return _MessageInstance;
    }

     public  static  Message create( long id,  Instant created,  User author,  Optional<String> content,  List<MessageEmbed> messageEmbeds,  List<Attachment> attachments){

        return create(() -> new Message(),id,created,author,content,messageEmbeds,attachments);
    }

     public  static  Message create( Supplier<? extends Message> _factoryMessage,  long id,  Instant created,  User author,  Optional<String> content,  List<MessageEmbed> messageEmbeds,  List<Attachment> attachments){

        Message _MessageInstance = _factoryMessage.get();
        _MessageInstance.setId(id);
        _MessageInstance.setCreated(created);
        _MessageInstance.setAuthor(author);
        _MessageInstance.setContent(content);
        _MessageInstance.setMessageEmbeds(messageEmbeds);
        _MessageInstance.setAttachments(attachments);
        return _MessageInstance;
    }

}