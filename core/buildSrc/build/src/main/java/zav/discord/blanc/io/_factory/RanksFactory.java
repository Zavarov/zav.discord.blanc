
package zav.discord.blanc.io._factory;

import com.google.common.collect.Multimap;
import java.lang.String;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;
import zav.discord.blanc.Rank;
import zav.discord.blanc.io.*;



 public  class RanksFactory   {
     public  static  Ranks create(){

        return create(() -> new Ranks());
    }

     public  static  Ranks create( Multimap<Long,Rank> ranks){

        return create(() -> new Ranks(),ranks);
    }

     public  static  Ranks create( Supplier<? extends Ranks> _factoryRanks){

        Ranks _RanksInstance = _factoryRanks.get();
        return _RanksInstance;
    }

     public  static  Ranks create( Supplier<? extends Ranks> _factoryRanks,  Multimap<Long,Rank> ranks){

        Ranks _RanksInstance = _factoryRanks.get();
        _RanksInstance.setRanks(ranks);
        return _RanksInstance;
    }

}