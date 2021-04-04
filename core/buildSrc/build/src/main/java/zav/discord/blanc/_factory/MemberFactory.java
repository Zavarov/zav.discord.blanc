
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



 public  class MemberFactory   {
     public  static  Member create( Supplier<? extends Member> _factoryMember,  OnlineStatus onlineStatus,  long id,  String name){

        Member _MemberInstance = _factoryMember.get();
        _MemberInstance.setOnlineStatus(onlineStatus);
        _MemberInstance.setId(id);
        _MemberInstance.setName(name);
        return _MemberInstance;
    }

     public  static  Member create( Supplier<? extends Member> _factoryMember,  OnlineStatus onlineStatus,  Collection<Rank> ranks,  long id,  String name){

        Member _MemberInstance = _factoryMember.get();
        _MemberInstance.setOnlineStatus(onlineStatus);
        _MemberInstance.setRanks(ranks);
        _MemberInstance.setId(id);
        _MemberInstance.setName(name);
        return _MemberInstance;
    }

}