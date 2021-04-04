
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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import zav.discord.blanc._visitor.ArchitectureVisitor;
import zav.discord.blanc.activity.Activity;
import zav.jra.models.AbstractLink;
import zav.jra.models.AbstractSubreddit;



 public  class Client   {
      private  List<Shard> shards = new java.util.concurrent.CopyOnWriteArrayList<>();
     public  void accept( ArchitectureVisitor visitor){

        visitor.handle(getRealThis());
    }

     public  Client getRealThis(){
        return this;
    }

     public  void notifyShards(){
        this.shards.notify();
    }

     public  boolean removeIfShards( Predicate<? super Shard> filter){
        return this.shards.removeIf(filter);
    }

     public  List<Shard> getShards(){
        return this.shards;
    }

     public  Spliterator<Shard> spliteratorShards(){
        return this.shards.spliterator();
    }

     public  ListIterator<Shard> listIteratorShards( int index){
        return this.shards.listIterator(index);
    }

     public  Shard getShards( int index){
        return this.shards.get(index);
    }

     public  void sortShards( Comparator<? super Shard> c){
        this.shards.sort(c);
    }

     public  int lastIndexOfShards( Object o){
        return this.shards.lastIndexOf(o);
    }

     public  boolean addAllShards( Collection<? extends Shard> c){
        return this.shards.addAll(c);
    }

     public  boolean removeAllShards( Collection<?> c){
        return this.shards.removeAll(c);
    }

     public  boolean removeShards( Object o){
        return this.shards.remove(o);
    }

     public  boolean equalsShards( Object obj){
        return this.shards.equals(obj);
    }

     public  boolean containsShards( Object o){
        return this.shards.contains(o);
    }

     public  boolean addAllShards( int index,  Collection<? extends Shard> c){
        return this.shards.addAll(index,c);
    }

     public  void setShards( List<Shard> shards){
        this.shards = shards;
    }

     public  boolean containsAllShards( Collection<?> c){
        return this.shards.containsAll(c);
    }

     public  Object[] toArrayShards(){
        return this.shards.toArray();
    }

     public  void notifyAllShards(){
        this.shards.notifyAll();
    }

     public  void waitShards()throws InterruptedException{
        this.shards.wait();
    }

     public  Stream<Shard> parallelStreamShards(){
        return this.shards.parallelStream();
    }

     public  boolean isEmptyShards(){
        return this.shards.isEmpty();
    }

     public  boolean addShards( Shard e){
        return this.shards.add(e);
    }

     public  Shard removeShards( int index){
        return this.shards.remove(index);
    }

     public  boolean retainAllShards( Collection<?> c){
        return this.shards.retainAll(c);
    }

     public  Shard[] toArrayShards( Shard[] a){
        return this.shards.toArray(a);
    }

     public  int sizeShards(){
        return this.shards.size();
    }

     public  Iterator<Shard> iteratorShards(){
        return this.shards.iterator();
    }

     public  List<Shard> subListShards( int fromIndex,  int toIndex){
        return this.shards.subList(fromIndex,toIndex);
    }

     public  Shard setShards( int index,  Shard element){
        return this.shards.set(index,element);
    }

     public  String toStringShards(){
        return this.shards.toString();
    }

     public  int indexOfShards( Object o){
        return this.shards.indexOf(o);
    }

     public  ListIterator<Shard> listIteratorShards(){
        return this.shards.listIterator();
    }

     public  int hashCodeShards(){
        return this.shards.hashCode();
    }

     public  void replaceAllShards( UnaryOperator<Shard> operator){
        this.shards.replaceAll(operator);
    }

     public  Shard[] toArrayShards( IntFunction<Shard[]> generator){
        return this.shards.toArray(generator);
    }

     public  void addShards( int index,  Shard element){
        this.shards.add(index,element);
    }

     public  void forEachShards( Consumer<? super Shard> action){
        this.shards.forEach(action);
    }

     public  void waitShards( long timeoutMillis)throws InterruptedException{
        this.shards.wait(timeoutMillis);
    }

     public  Stream<Shard> streamShards(){
        return this.shards.stream();
    }

     public  void waitShards( long timeoutMillis,  int nanos)throws InterruptedException{
        this.shards.wait(timeoutMillis,nanos);
    }

     public  void clearShards(){
        this.shards.clear();
    }

}