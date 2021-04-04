
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



 public  class GuildActivityFactory   {
     public  static  GuildActivity create( long membersCount,  long membersOnline,  double activity){

        return create(() -> new GuildActivity(),membersCount,membersOnline,activity);
    }

     public  static  GuildActivity create( Supplier<? extends GuildActivity> _factoryGuildActivity,  long membersCount,  long membersOnline,  double activity){

        GuildActivity _GuildActivityInstance = _factoryGuildActivity.get();
        _GuildActivityInstance.setMembersCount(membersCount);
        _GuildActivityInstance.setMembersOnline(membersOnline);
        _GuildActivityInstance.setActivity(activity);
        return _GuildActivityInstance;
    }

     public  static  GuildActivity create( long membersCount,  long membersOnline,  double activity,  Map<TextChannel,Double> channelActivity){

        return create(() -> new GuildActivity(),membersCount,membersOnline,activity,channelActivity);
    }

     public  static  GuildActivity create( Supplier<? extends GuildActivity> _factoryGuildActivity,  long membersCount,  long membersOnline,  double activity,  Map<TextChannel,Double> channelActivity){

        GuildActivity _GuildActivityInstance = _factoryGuildActivity.get();
        _GuildActivityInstance.setMembersCount(membersCount);
        _GuildActivityInstance.setMembersOnline(membersOnline);
        _GuildActivityInstance.setActivity(activity);
        _GuildActivityInstance.setChannelActivity(channelActivity);
        return _GuildActivityInstance;
    }

}