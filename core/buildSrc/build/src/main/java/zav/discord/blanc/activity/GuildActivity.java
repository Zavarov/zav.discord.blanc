
package zav.discord.blanc.activity;

import com.google.common.cache.Cache;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.lang.Class;
import java.lang.Double;
import java.lang.InterruptedException;
import java.lang.Object;
import java.lang.String;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import zav.discord.blanc.Guild;
import zav.discord.blanc.TextChannel;
import zav.discord.blanc.activity._visitor.ActivityVisitor;



 public  class GuildActivity   {
      private  long membersCount ;
      private  long membersOnline ;
      private  double activity ;
      private  Map<TextChannel,Double> channelActivity = new java.util.concurrent.ConcurrentHashMap<>();
     public  void accept( ActivityVisitor visitor){

        visitor.handle(getRealThis());
    }

     public  GuildActivity getRealThis(){
        return this;
    }

     public  void notifyChannelActivity(){
        this.channelActivity.notify();
    }

     public  void setMembersCount( long membersCount){
        this.membersCount = membersCount;
    }

     public  void clearChannelActivity(){
        this.channelActivity.clear();
    }

     public  void forEachChannelActivity( BiConsumer<? super TextChannel,? super Double> action){
        this.channelActivity.forEach(action);
    }

     public  Set<Map.Entry<TextChannel,Double>> entrySetChannelActivity(){
        return this.channelActivity.entrySet();
    }

     public  void setChannelActivity( Map<TextChannel,Double> channelActivity){
        this.channelActivity = channelActivity;
    }

     public  Double computeIfPresentChannelActivity( TextChannel key,  BiFunction<? super TextChannel,? super Double,? extends Double> remappingFunction){
        return this.channelActivity.computeIfPresent(key,remappingFunction);
    }

     public  boolean equalsChannelActivity( Object obj){
        return this.channelActivity.equals(obj);
    }

     public  Collection<Double> valuesChannelActivity(){
        return this.channelActivity.values();
    }

     public  Double getOrDefaultChannelActivity( Object key,  Double defaultValue){
        return this.channelActivity.getOrDefault(key,defaultValue);
    }

     public  Double computeChannelActivity( TextChannel key,  BiFunction<? super TextChannel,? super Double,? extends Double> remappingFunction){
        return this.channelActivity.compute(key,remappingFunction);
    }

     public  void notifyAllChannelActivity(){
        this.channelActivity.notifyAll();
    }

     public  Double putIfAbsentChannelActivity( TextChannel key,  Double value){
        return this.channelActivity.putIfAbsent(key,value);
    }

     public  int sizeChannelActivity(){
        return this.channelActivity.size();
    }

     public  void setActivity( double activity){
        this.activity = activity;
    }

     public  boolean containsKeyChannelActivity( Object object){
        return this.channelActivity.containsKey(object);
    }

     public  Map<TextChannel,Double> getChannelActivity(){
        return this.channelActivity;
    }

     public  boolean removeChannelActivity( Object key,  Object value){
        return this.channelActivity.remove(key,value);
    }

     public  Double computeIfAbsentChannelActivity( TextChannel key,  Function<? super TextChannel,? extends Double> mappingFunction){
        return this.channelActivity.computeIfAbsent(key,mappingFunction);
    }

     public  long getMembersOnline(){
        return this.membersOnline;
    }

     public  Double putChannelActivity( TextChannel key,  Double value){
        return this.channelActivity.put(key,value);
    }

     public  double getActivity(){
        return this.activity;
    }

     public  long getMembersCount(){
        return this.membersCount;
    }

     public  Double mergeChannelActivity( TextChannel key,  Double value,  BiFunction<? super Double,? super Double,? extends Double> remappingFunction){
        return this.channelActivity.merge(key,value,remappingFunction);
    }

     public  void putAllChannelActivity( Map<? extends TextChannel,? extends Double> m){
        this.channelActivity.putAll(m);
    }

     public  Double replaceChannelActivity( TextChannel key,  Double value){
        return this.channelActivity.replace(key,value);
    }

     public  void setMembersOnline( long membersOnline){
        this.membersOnline = membersOnline;
    }

     public  boolean isEmptyChannelActivity(){
        return this.channelActivity.isEmpty();
    }

     public  int hashCodeChannelActivity(){
        return this.channelActivity.hashCode();
    }

     public  Set<TextChannel> keySetChannelActivity(){
        return this.channelActivity.keySet();
    }

     public  boolean replaceChannelActivity( TextChannel key,  Double oldValue,  Double newValue){
        return this.channelActivity.replace(key,oldValue,newValue);
    }

     public  void waitChannelActivity()throws InterruptedException{
        this.channelActivity.wait();
    }

     public  void waitChannelActivity( long timeoutMillis)throws InterruptedException{
        this.channelActivity.wait(timeoutMillis);
    }

     public  void waitChannelActivity( long timeoutMillis,  int nanos)throws InterruptedException{
        this.channelActivity.wait(timeoutMillis,nanos);
    }

     public  boolean containsValueChannelActivity( Object object){
        return this.channelActivity.containsValue(object);
    }

     public  Double getChannelActivity( Object key){
        return this.channelActivity.get(key);
    }

     public  void replaceAllChannelActivity( BiFunction<? super TextChannel,? super Double,? extends Double> function){
        this.channelActivity.replaceAll(function);
    }

     public  String toStringChannelActivity(){
        return this.channelActivity.toString();
    }

     public  Double removeChannelActivity( Object key){
        return this.channelActivity.remove(key);
    }

}