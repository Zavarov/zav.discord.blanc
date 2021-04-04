
package zav.discord.blanc.io;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import java.lang.Iterable;
import java.lang.Object;
import java.lang.String;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import zav.discord.blanc.Rank;
import zav.discord.blanc.io._visitor.IOVisitor;



 public  class Ranks   {
      private  Multimap<Long,Rank> ranks = com.google.common.collect.HashMultimap.create();;
     public  void accept( IOVisitor visitor){

        visitor.handle(getRealThis());
    }

     public  Ranks getRealThis(){
        return this;
    }

     public  Set<Long> keySetRanks(){
        return this.ranks.keySet();
    }

     public  Collection<Rank> valuesRanks(){
        return this.ranks.values();
    }

     public  Collection<Rank> replaceValuesRanks( Long key,  Iterable<? extends Rank> values){
        return this.ranks.replaceValues(key,values);
    }

     public  int sizeRanks(){
        return this.ranks.size();
    }

     public  boolean isEmptyRanks(){
        return this.ranks.isEmpty();
    }

     public  boolean containsKeyRanks( Object key){
        return this.ranks.containsKey(key);
    }

     public  Collection<Rank> getRanks( Long key){
        return this.ranks.get(key);
    }

     public  Map<Long,Collection<Rank>> asMapRanks(){
        return this.ranks.asMap();
    }

     public  void clearRanks(){
        this.ranks.clear();
    }

     public  int hashCodeRanks(){
        return this.ranks.hashCode();
    }

     public  void forEachRanks( BiConsumer<? super Long,? super Rank> action){
        this.ranks.forEach(action);
    }

     public  boolean containsEntryRanks( Object key,  Object value){
        return this.ranks.containsEntry(key,value);
    }

     public  boolean putRanks( Long key,  Rank value){
        return this.ranks.put(key,value);
    }

     public  Multiset<Long> keysRanks(){
        return this.ranks.keys();
    }

     public  boolean putAllRanks( Multimap<? extends Long,? extends Rank> multimap){
        return this.ranks.putAll(multimap);
    }

     public  void setRanks( Multimap<Long,Rank> ranks){
        this.ranks = ranks;
    }

     public  Collection<Map.Entry<Long,Rank>> entriesRanks(){
        return this.ranks.entries();
    }

     public  Multimap<Long,Rank> getRanks(){
        return this.ranks;
    }

     public  Collection<Rank> removeAllRanks( Object key){
        return this.ranks.removeAll(key);
    }

     public  boolean removeRanks( Object key,  Object value){
        return this.ranks.remove(key,value);
    }

     public  boolean containsValueRanks( Object value){
        return this.ranks.containsValue(value);
    }

     public  boolean equalsRanks( Object obj){
        return this.ranks.equals(obj);
    }

     public  boolean putAllRanks( Long key,  Iterable<? extends Rank> values){
        return this.ranks.putAll(key,values);
    }

}