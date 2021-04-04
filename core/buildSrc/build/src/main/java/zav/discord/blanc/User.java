
package zav.discord.blanc;

import com.google.common.cache.Cache;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Class;
import java.lang.InterruptedException;
import java.lang.Iterable;
import java.lang.Object;
import java.lang.RuntimeException;
import java.lang.String;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import zav.discord.blanc.Attachment;
import zav.discord.blanc.Author;
import zav.discord.blanc.Client;
import zav.discord.blanc.ConfigurationModule;
import zav.discord.blanc.Field;
import zav.discord.blanc.Guild;
import zav.discord.blanc.Killable;
import zav.discord.blanc.Member;
import zav.discord.blanc.Message;
import zav.discord.blanc.MessageChannel;
import zav.discord.blanc.MessageEmbed;
import zav.discord.blanc.OnlineStatus;
import zav.discord.blanc.Permission;
import zav.discord.blanc.Printable;
import zav.discord.blanc.PrivateChannel;
import zav.discord.blanc.Rank;
import zav.discord.blanc.Role;
import zav.discord.blanc.SelfMember;
import zav.discord.blanc.SelfUser;
import zav.discord.blanc.Shard;
import zav.discord.blanc.Snowflake;
import zav.discord.blanc.TextChannel;
import zav.discord.blanc.Title;
import zav.discord.blanc.User;
import zav.discord.blanc.Webhook;
import zav.discord.blanc._visitor.ArchitectureVisitor;
import zav.discord.blanc.activity.Activity;
import zav.jra.models.AbstractLink;
import zav.jra.models.AbstractSubreddit;



 abstract  public  class User  extends Snowflake  implements Printable {
      private  OnlineStatus onlineStatus ;
      private  Collection<Rank> ranks = new java.util.concurrent.CopyOnWriteArrayList<>();
     abstract  public  PrivateChannel retrievePrivateChannel();

     abstract  public  String getAsMention();

     public  void accept( ArchitectureVisitor visitor){

        visitor.handle(getRealThis());
    }

     public  User getRealThis(){
        return this;
    }

     public  void waitRanks( long timeoutMillis,  int nanos)throws InterruptedException{
        this.ranks.wait(timeoutMillis,nanos);
    }

     public  void forEachRanks( Consumer<? super Rank> action){
        this.ranks.forEach(action);
    }

     public  Collection<Rank> getRanks(){
        return this.ranks;
    }

     public  void acceptOnlineStatus( ArchitectureVisitor visitor){
        this.onlineStatus.accept(visitor);
    }

     public  int sizeRanks(){
        return this.ranks.size();
    }

     public  Rank[] toArrayRanks( Rank[] a){
        return this.ranks.toArray(a);
    }

     public  boolean containsRanks( Object o){
        return this.ranks.contains(o);
    }

     public  OnlineStatus getOnlineStatus(){
        return this.onlineStatus;
    }

     public  boolean isEmptyRanks(){
        return this.ranks.isEmpty();
    }

     public  boolean removeRanks( Object o){
        return this.ranks.remove(o);
    }

     public  boolean containsAllRanks( Collection<?> c){
        return this.ranks.containsAll(c);
    }

     public  boolean removeAllRanks( Collection<?> c){
        return this.ranks.removeAll(c);
    }

     public  void setRanks( Collection<Rank> ranks){
        this.ranks = ranks;
    }

     public  void clearRanks(){
        this.ranks.clear();
    }

     public  Stream<Rank> parallelStreamRanks(){
        return this.ranks.parallelStream();
    }

     public  boolean removeIfRanks( Predicate<? super Rank> filter){
        return this.ranks.removeIf(filter);
    }

     public  Object[] toArrayRanks(){
        return this.ranks.toArray();
    }

     public  void waitRanks()throws InterruptedException{
        this.ranks.wait();
    }

     public  boolean retainAllRanks( Collection<?> c){
        return this.ranks.retainAll(c);
    }

     public  void notifyRanks(){
        this.ranks.notify();
    }

     public  boolean addAllRanks( Collection<? extends Rank> c){
        return this.ranks.addAll(c);
    }

     public  boolean addRanks( Rank e){
        return this.ranks.add(e);
    }

     public  OnlineStatus getRealThisOnlineStatus(){
        return this.onlineStatus.getRealThis();
    }

     public  Spliterator<Rank> spliteratorRanks(){
        return this.ranks.spliterator();
    }

     public  Stream<Rank> streamRanks(){
        return this.ranks.stream();
    }

     public  void notifyAllRanks(){
        this.ranks.notifyAll();
    }

     public  Iterator<Rank> iteratorRanks(){
        return this.ranks.iterator();
    }

     public  void setOnlineStatus( OnlineStatus onlineStatus){
        this.onlineStatus = onlineStatus;
    }

     public  int hashCodeRanks(){
        return this.ranks.hashCode();
    }

     public  void waitRanks( long timeoutMillis)throws InterruptedException{
        this.ranks.wait(timeoutMillis);
    }

     public  Rank[] toArrayRanks( IntFunction<Rank[]> generator){
        return this.ranks.toArray(generator);
    }

     public  String toStringRanks(){
        return this.ranks.toString();
    }

     public  boolean equalsRanks( Object obj){
        return this.ranks.equals(obj);
    }

}