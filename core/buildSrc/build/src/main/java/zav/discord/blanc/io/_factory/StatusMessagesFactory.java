
package zav.discord.blanc.io._factory;

import com.google.common.collect.Multimap;
import java.lang.String;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;
import zav.discord.blanc.Rank;
import zav.discord.blanc.io.*;



 public  class StatusMessagesFactory   {
     public  static  StatusMessages create(){

        return create(() -> new StatusMessages());
    }

     public  static  StatusMessages create( List<String> statusMessages){

        return create(() -> new StatusMessages(),statusMessages);
    }

     public  static  StatusMessages create( Supplier<? extends StatusMessages> _factoryStatusMessages){

        StatusMessages _StatusMessagesInstance = _factoryStatusMessages.get();
        return _StatusMessagesInstance;
    }

     public  static  StatusMessages create( Supplier<? extends StatusMessages> _factoryStatusMessages,  List<String> statusMessages){

        StatusMessages _StatusMessagesInstance = _factoryStatusMessages.get();
        _StatusMessagesInstance.setStatusMessages(statusMessages);
        return _StatusMessagesInstance;
    }

}