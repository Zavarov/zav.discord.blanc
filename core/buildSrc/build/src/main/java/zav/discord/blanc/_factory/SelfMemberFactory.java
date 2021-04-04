
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



 public  class SelfMemberFactory   {
     public  static  SelfMember create( Supplier<? extends SelfMember> _factorySelfMember,  OnlineStatus onlineStatus,  long id,  String name){

        SelfMember _SelfMemberInstance = _factorySelfMember.get();
        _SelfMemberInstance.setOnlineStatus(onlineStatus);
        _SelfMemberInstance.setId(id);
        _SelfMemberInstance.setName(name);
        return _SelfMemberInstance;
    }

     public  static  SelfMember create( Supplier<? extends SelfMember> _factorySelfMember,  OnlineStatus onlineStatus,  Collection<Rank> ranks,  long id,  String name){

        SelfMember _SelfMemberInstance = _factorySelfMember.get();
        _SelfMemberInstance.setOnlineStatus(onlineStatus);
        _SelfMemberInstance.setRanks(ranks);
        _SelfMemberInstance.setId(id);
        _SelfMemberInstance.setName(name);
        return _SelfMemberInstance;
    }

}