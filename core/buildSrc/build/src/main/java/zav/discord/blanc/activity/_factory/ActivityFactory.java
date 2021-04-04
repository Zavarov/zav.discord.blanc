
package zav.discord.blanc.activity._factory;

import com.google.common.cache.Cache;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.lang.Double;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import zav.discord.blanc.Guild;
import zav.discord.blanc.TextChannel;
import zav.discord.blanc.activity.*;



 public  class ActivityFactory   {
     public  static  Activity create( Supplier<? extends Activity> _factoryActivity){

        Activity _ActivityInstance = _factoryActivity.get();
        return _ActivityInstance;
    }

     public  static  Activity create( Supplier<? extends Activity> _factoryActivity,  Cache<LocalDateTime,GuildActivity> activity){

        Activity _ActivityInstance = _factoryActivity.get();
        _ActivityInstance.setActivity(activity);
        return _ActivityInstance;
    }

}