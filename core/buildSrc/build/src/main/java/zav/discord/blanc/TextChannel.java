
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
import zav.discord.blanc._visitor.ArchitectureVisitor;
import zav.discord.blanc.activity.Activity;
import zav.jra.models.AbstractLink;
import zav.jra.models.AbstractSubreddit;



 abstract  public  class TextChannel  extends MessageChannel  {
      private  Set<String> subreddits = new java.util.concurrent.CopyOnWriteArraySet<>();
     abstract  public  Webhook createWebhook( String name);

     abstract  public  Collection<Webhook> retrieveWebhooks( String name);

     abstract  public  Collection<Webhook> retrieveWebhooks();

     abstract  public  String getAsMention();

     public  void accept( ArchitectureVisitor visitor){

        visitor.handle(getRealThis());
    }

     public  TextChannel getRealThis(){
        return this;
    }

     public  void clearSubreddits(){
        this.subreddits.clear();
    }

     public  Iterator<String> iteratorSubreddits(){
        return this.subreddits.iterator();
    }

     public  String toStringSubreddits(){
        return this.subreddits.toString();
    }

     public  boolean retainAllSubreddits( Collection<?> c){
        return this.subreddits.retainAll(c);
    }

     public  int sizeSubreddits(){
        return this.subreddits.size();
    }

     public  void waitSubreddits()throws InterruptedException{
        this.subreddits.wait();
    }

     public  boolean containsSubreddits( Object o){
        return this.subreddits.contains(o);
    }

     public  boolean removeSubreddits( Object o){
        return this.subreddits.remove(o);
    }

     public  boolean containsAllSubreddits( Collection<?> c){
        return this.subreddits.containsAll(c);
    }

     public  Stream<String> parallelStreamSubreddits(){
        return this.subreddits.parallelStream();
    }

     public  int hashCodeSubreddits(){
        return this.subreddits.hashCode();
    }

     public  boolean equalsSubreddits( Object obj){
        return this.subreddits.equals(obj);
    }

     public  String[] toArraySubreddits( IntFunction<String[]> generator){
        return this.subreddits.toArray(generator);
    }

     public  Set<String> getSubreddits(){
        return this.subreddits;
    }

     public  void forEachSubreddits( Consumer<? super String> action){
        this.subreddits.forEach(action);
    }

     public  void notifySubreddits(){
        this.subreddits.notify();
    }

     public  boolean isEmptySubreddits(){
        return this.subreddits.isEmpty();
    }

     public  void notifyAllSubreddits(){
        this.subreddits.notifyAll();
    }

     public  boolean removeIfSubreddits( Predicate<? super String> filter){
        return this.subreddits.removeIf(filter);
    }

     public  boolean removeAllSubreddits( Collection<?> c){
        return this.subreddits.removeAll(c);
    }

     public  void setSubreddits( Set<String> subreddits){
        this.subreddits = subreddits;
    }

     public  String[] toArraySubreddits( String[] a){
        return this.subreddits.toArray(a);
    }

     public  void waitSubreddits( long timeoutMillis)throws InterruptedException{
        this.subreddits.wait(timeoutMillis);
    }

     public  Spliterator<String> spliteratorSubreddits(){
        return this.subreddits.spliterator();
    }

     public  void waitSubreddits( long timeoutMillis,  int nanos)throws InterruptedException{
        this.subreddits.wait(timeoutMillis,nanos);
    }

     public  boolean addAllSubreddits( Collection<? extends String> c){
        return this.subreddits.addAll(c);
    }

     public  boolean addSubreddits( String e){
        return this.subreddits.add(e);
    }

     public  Stream<String> streamSubreddits(){
        return this.subreddits.stream();
    }

     public  Object[] toArraySubreddits(){
        return this.subreddits.toArray();
    }

}