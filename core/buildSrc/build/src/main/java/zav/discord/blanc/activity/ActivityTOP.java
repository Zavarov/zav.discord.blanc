
package zav.discord.blanc.activity;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheStats;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.lang.Class;
import java.lang.Double;
import java.lang.InterruptedException;
import java.lang.Iterable;
import java.lang.Object;
import java.lang.String;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import zav.discord.blanc.Guild;
import zav.discord.blanc.TextChannel;
import zav.discord.blanc.activity._visitor.ActivityVisitor;



 abstract  public  class ActivityTOP   {
      private  Cache<LocalDateTime,GuildActivity> activity = com.google.common.cache.CacheBuilder.newBuilder().expireAfterAccess(java.time.Duration.parse("P1D")).build();
     public  BufferedImage build( List<TextChannel> channels,  Rectangle bounds){
        throw new UnsupportedOperationException();
    }

     abstract  public  void update( Guild guild);

     public  void accept( ActivityVisitor visitor){

        visitor.handle(getRealThis());
    }

     abstract  public  Activity getRealThis();

     public  Cache<LocalDateTime,GuildActivity> getActivity(){
        return this.activity;
    }

     public  boolean equalsActivity( Object obj){
        return this.activity.equals(obj);
    }

     public  void waitActivity()throws InterruptedException{
        this.activity.wait();
    }

     public  void invalidateAllActivity( Iterable<?> keys){
        this.activity.invalidateAll(keys);
    }

     public  ConcurrentMap<LocalDateTime,GuildActivity> asMapActivity(){
        return this.activity.asMap();
    }

     public  String toStringActivity(){
        return this.activity.toString();
    }

     public  GuildActivity getUncheckedActivity( LocalDateTime key)throws UncheckedExecutionException{

        try{
            return getActivity(key);
        }catch(ExecutionException e){
            throw new UncheckedExecutionException(e.getCause());
        }
    }

     public  GuildActivity getIfPresentActivity( Object key){
        return this.activity.getIfPresent(key);
    }

     public  void cleanUpActivity(){
        this.activity.cleanUp();
    }

     public  void invalidateActivity( Object key){
        this.activity.invalidate(key);
    }

     public  void notifyAllActivity(){
        this.activity.notifyAll();
    }

     public  ImmutableMap<LocalDateTime,GuildActivity> getAllPresentActivity( Iterable<?> keys){
        return this.activity.getAllPresent(keys);
    }

     public  void setActivity( Cache<LocalDateTime,GuildActivity> activity){
        this.activity = activity;
    }

     public  Collection<GuildActivity> valuesActivity(){

        return this.activity.asMap().values();
    }

     public  void notifyActivity(){
        this.activity.notify();
    }

     public  void waitActivity( long timeoutMillis)throws InterruptedException{
        this.activity.wait(timeoutMillis);
    }

     public  void putActivity( LocalDateTime key,  GuildActivity value){
        this.activity.put(key,value);
    }

     public  void waitActivity( long timeoutMillis,  int nanos)throws InterruptedException{
        this.activity.wait(timeoutMillis,nanos);
    }

     public  Set<LocalDateTime> keysActivity(){

        return this.activity.asMap().keySet();
    }

     public  void putAllActivity( Map<? extends LocalDateTime,? extends GuildActivity> m){
        this.activity.putAll(m);
    }

     public  int hashCodeActivity(){
        return this.activity.hashCode();
    }

     public  GuildActivity getActivity( LocalDateTime key)throws ExecutionException{

        throw new UnsupportedOperationException("Please overwrite this method and provide a loader.");

    }

     public  void invalidateAllActivity(){
        this.activity.invalidateAll();
    }

     public  GuildActivity getActivity( LocalDateTime key,  Callable<? extends GuildActivity> loader)throws ExecutionException{
        return this.activity.get(key,loader);
    }

     public  CacheStats statsActivity(){
        return this.activity.stats();
    }

     public  long sizeActivity(){
        return this.activity.size();
    }

}