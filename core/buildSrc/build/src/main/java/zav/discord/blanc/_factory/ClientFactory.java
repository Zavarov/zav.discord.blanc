
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



 public  class ClientFactory   {
     public  static  Client create(){

        return create(() -> new Client());
    }

     public  static  Client create( List<Shard> shards){

        return create(() -> new Client(),shards);
    }

     public  static  Client create( Supplier<? extends Client> _factoryClient){

        Client _ClientInstance = _factoryClient.get();
        return _ClientInstance;
    }

     public  static  Client create( Supplier<? extends Client> _factoryClient,  List<Shard> shards){

        Client _ClientInstance = _factoryClient.get();
        _ClientInstance.setShards(shards);
        return _ClientInstance;
    }

}