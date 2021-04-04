
package zav.discord.blanc;

import com.google.common.cache.Cache;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Class;
import java.lang.Double;
import java.lang.InterruptedException;
import java.lang.Iterable;
import java.lang.Object;
import java.lang.Runnable;
import java.lang.RuntimeException;
import java.lang.String;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import zav.discord.blanc.Guild;
import zav.discord.blanc.TextChannel;
import zav.discord.blanc._visitor.ArchitectureVisitor;
import zav.discord.blanc.activity.Activity;
import zav.discord.blanc.activity.GuildActivity;
import zav.jra.models.AbstractLink;
import zav.jra.models.AbstractSubreddit;



 abstract  public  class GuildTOP  extends Snowflake  implements Printable {
      private  Optional<String> prefix = java.util.Optional.empty();
      private  List<String> blacklist = new java.util.concurrent.CopyOnWriteArrayList<>();
      private  Activity activity ;
     abstract  public  Optional<Member> retrieveMember( long id);

     abstract  public  Collection<Member> retrieveMembers();

     abstract  public  Optional<TextChannel> retrieveTextChannel( long id);

     abstract  public  Collection<TextChannel> retrieveTextChannels();

     abstract  public  Optional<Role> retrieveRole( long id);

     abstract  public  Collection<Role> retrieveRoles();

     abstract  public  SelfMember retrieveSelfMember();

     abstract  public  void leave();

     public  void accept( ArchitectureVisitor visitor){

        visitor.handle(getRealThis());
    }

     abstract  public  Guild getRealThis();

     public  Optional<String> getPrefix(){
        return this.prefix;
    }

     public  int hashCodeBlacklist(){
        return this.blacklist.hashCode();
    }

     public  int hashCodePrefix(){
        return this.prefix.hashCode();
    }

     public  void notifyAllPrefix(){
        this.prefix.notifyAll();
    }

     public  void clearBlacklist(){
        this.blacklist.clear();
    }

     public  String removeBlacklist( int index){
        return this.blacklist.remove(index);
    }

     public  boolean removeAllBlacklist( Collection<?> c){
        return this.blacklist.removeAll(c);
    }

     public  void waitBlacklist()throws InterruptedException{
        this.blacklist.wait();
    }

     public  void waitBlacklist( long timeoutMillis,  int nanos)throws InterruptedException{
        this.blacklist.wait(timeoutMillis,nanos);
    }

     public  Spliterator<String> spliteratorBlacklist(){
        return this.blacklist.spliterator();
    }

     public  void waitBlacklist( long timeoutMillis)throws InterruptedException{
        this.blacklist.wait(timeoutMillis);
    }

     public  String[] toArrayBlacklist( IntFunction<String[]> generator){
        return this.blacklist.toArray(generator);
    }

     public  void forEachBlacklist( Consumer<? super String> action){
        this.blacklist.forEach(action);
    }

     public  Iterator<String> iteratorBlacklist(){
        return this.blacklist.iterator();
    }

     public  boolean isPresentPrefix(){
        return this.prefix.isPresent();
    }

     public  void replaceAllBlacklist( UnaryOperator<String> operator){
        this.blacklist.replaceAll(operator);
    }

     public  void updateActivity( Guild guild){
        this.activity.update(guild);
    }

     public  String[] toArrayBlacklist( String[] a){
        return this.blacklist.toArray(a);
    }

     public  BufferedImage buildActivity( List<TextChannel> channels,  Rectangle bounds){
        return this.activity.build(channels,bounds);
    }

     public  boolean addAllBlacklist( int index,  Collection<? extends String> c){
        return this.blacklist.addAll(index,c);
    }

     public  String orElsePrefix( String other){
        return this.prefix.orElse(other);
    }

     public  Stream<String> streamBlacklist(){
        return this.blacklist.stream();
    }

     public  int lastIndexOfBlacklist( Object o){
        return this.blacklist.lastIndexOf(o);
    }

     public  void setPrefix( Optional<String> prefix){
        this.prefix = prefix;
    }

     public  void ifPresentOrElsePrefix( Consumer<? super String> action,  Runnable emptyAction){
        this.prefix.ifPresentOrElse(action,emptyAction);
    }

     public  ListIterator<String> listIteratorBlacklist( int index){
        return this.blacklist.listIterator(index);
    }

     public  void notifyBlacklist(){
        this.blacklist.notify();
    }

     public  String orElseGetPrefix( Supplier<? extends String> supplier){
        return this.prefix.orElseGet(supplier);
    }

     public  String orElseThrowPrefix(){
        return this.prefix.orElseThrow();
    }

     public  boolean removeBlacklist( Object o){
        return this.blacklist.remove(o);
    }

     public  boolean containsAllBlacklist( Collection<?> c){
        return this.blacklist.containsAll(c);
    }

     public  void ifPresentPrefix( Consumer<? super String> action){
        this.prefix.ifPresent(action);
    }

     public  boolean equalsBlacklist( Object obj){
        return this.blacklist.equals(obj);
    }

     public  boolean equalsPrefix( Object obj){
        return this.prefix.equals(obj);
    }

     public  Optional<String> filterPrefix( Predicate<? super String> predicate){
        return this.prefix.filter(predicate);
    }

     public  void setPrefix( String newValue){

        this.prefix = Optional.ofNullable(newValue);
    }

     public  String setBlacklist( int index,  String element){
        return this.blacklist.set(index,element);
    }

     public  boolean addAllBlacklist( Collection<? extends String> c){
        return this.blacklist.addAll(c);
    }

     public  void setActivity( Activity activity){
        this.activity = activity;
    }

     public  void waitPrefix( long timeoutMillis)throws InterruptedException{
        this.prefix.wait(timeoutMillis);
    }

     public  Stream<String> parallelStreamBlacklist(){
        return this.blacklist.parallelStream();
    }

     public  int indexOfBlacklist( Object o){
        return this.blacklist.indexOf(o);
    }

     public  boolean containsBlacklist( Object o){
        return this.blacklist.contains(o);
    }

     public  Stream<String> streamPrefix(){
        return this.prefix.stream();
    }

     public  boolean retainAllBlacklist( Collection<?> c){
        return this.blacklist.retainAll(c);
    }

     public  void notifyPrefix(){
        this.prefix.notify();
    }

     public  void waitPrefix( long timeoutMillis,  int nanos)throws InterruptedException{
        this.prefix.wait(timeoutMillis,nanos);
    }

     public  String toStringBlacklist(){
        return this.blacklist.toString();
    }

     public  List<String> subListBlacklist( int fromIndex,  int toIndex){
        return this.blacklist.subList(fromIndex,toIndex);
    }

     public  Object[] toArrayBlacklist(){
        return this.blacklist.toArray();
    }

     public  void sortBlacklist( Comparator<? super String> c){
        this.blacklist.sort(c);
    }

     public  Activity getActivity(){
        return this.activity;
    }

     public  int sizeBlacklist(){
        return this.blacklist.size();
    }

     public  void waitPrefix()throws InterruptedException{
        this.prefix.wait();
    }

     public  String toStringPrefix(){
        return this.prefix.toString();
    }

     public  boolean addBlacklist( String e){
        return this.blacklist.add(e);
    }

     public  boolean isEmptyPrefix(){
        return this.prefix.isEmpty();
    }

     public  ListIterator<String> listIteratorBlacklist(){
        return this.blacklist.listIterator();
    }

     public  boolean removeIfBlacklist( Predicate<? super String> filter){
        return this.blacklist.removeIf(filter);
    }

     public  String getBlacklist( int index){
        return this.blacklist.get(index);
    }

     public  boolean isEmptyBlacklist(){
        return this.blacklist.isEmpty();
    }

     public  Optional<String> orPrefix( Supplier<? extends Optional<? extends String>> supplier){
        return this.prefix.or(supplier);
    }

     public  List<String> getBlacklist(){
        return this.blacklist;
    }

     public  void notifyAllBlacklist(){
        this.blacklist.notifyAll();
    }

     public  void addBlacklist( int index,  String element){
        this.blacklist.add(index,element);
    }

     public  void setBlacklist( List<String> blacklist){
        this.blacklist = blacklist;
    }

}