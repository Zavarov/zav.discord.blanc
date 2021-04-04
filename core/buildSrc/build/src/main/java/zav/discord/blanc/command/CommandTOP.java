
package zav.discord.blanc.command;

import com.google.common.cache.Cache;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Class;
import java.lang.InterruptedException;
import java.lang.Iterable;
import java.lang.Object;
import java.lang.Runnable;
import java.lang.RuntimeException;
import java.lang.String;
import java.lang.Throwable;
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
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
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
import zav.discord.blanc.activity.Activity;
import zav.discord.blanc.command._visitor.CommandVisitor;
import zav.discord.blanc.parser.AbstractTypeResolver;
import zav.discord.blanc.parser.Argument;
import zav.jra.models.AbstractLink;
import zav.jra.models.AbstractSubreddit;



 abstract  public  class CommandTOP   implements Runnable {
      private  Shard $Shard ;
      private  Message $Message ;
      private  List<String> $Flags = new java.util.concurrent.CopyOnWriteArrayList<>();
     abstract  public  void validate();

     public  void accept( CommandVisitor visitor){

        visitor.handle(getRealThis());
    }

     abstract  public  Command getRealThis();

     public  void set$Message( Message $Message){
        this.$Message = $Message;
    }

     public  Spliterator<String> spliterator$Flags(){
        return this.$Flags.spliterator();
    }

     public  String[] toArray$Flags( IntFunction<String[]> generator){
        return this.$Flags.toArray(generator);
    }

     public  void shutdown$Shard(){
        this.$Shard.shutdown();
    }

     public  String toString$Flags(){
        return this.$Flags.toString();
    }

     public  boolean containsAll$Flags( Collection<?> c){
        return this.$Flags.containsAll(c);
    }

     public  void sort$Flags( Comparator<? super String> c){
        this.$Flags.sort(c);
    }

     public  ListIterator<String> listIterator$Flags( int index){
        return this.$Flags.listIterator(index);
    }

     public  Collection<Guild> retrieveGuilds$Shard(){
        return this.$Shard.retrieveGuilds();
    }

     public  void set$Flags( List<String> $Flags){
        this.$Flags = $Flags;
    }

     public  Object[] toArray$Flags(){
        return this.$Flags.toArray();
    }

     public  Collection<User> retrieveUsers$Shard(){
        return this.$Shard.retrieveUsers();
    }

     public  String[] toArray$Flags( String[] a){
        return this.$Flags.toArray(a);
    }

     public  void wait$Flags( long timeoutMillis)throws InterruptedException{
        this.$Flags.wait(timeoutMillis);
    }

     public  int lastIndexOf$Flags( Object o){
        return this.$Flags.lastIndexOf(o);
    }

     public  boolean contains$Flags( Object o){
        return this.$Flags.contains(o);
    }

     public  void wait$Flags()throws InterruptedException{
        this.$Flags.wait();
    }

     public  boolean addAll$Flags( Collection<? extends String> c){
        return this.$Flags.addAll(c);
    }

     public  Message get$Message(){
        return this.$Message;
    }

     public  boolean retainAll$Flags( Collection<?> c){
        return this.$Flags.retainAll(c);
    }

     public  List<String> subList$Flags( int fromIndex,  int toIndex){
        return this.$Flags.subList(fromIndex,toIndex);
    }

     public  Stream<String> stream$Flags(){
        return this.$Flags.stream();
    }

     public  List<String> get$Flags(){
        return this.$Flags;
    }

     public  ListIterator<String> listIterator$Flags(){
        return this.$Flags.listIterator();
    }

     public  Shard get$Shard(){
        return this.$Shard;
    }

     public  String set$Flags( int index,  String element){
        return this.$Flags.set(index,element);
    }

     public  void replaceAll$Flags( UnaryOperator<String> operator){
        this.$Flags.replaceAll(operator);
    }

     public  int hashCode$Flags(){
        return this.$Flags.hashCode();
    }

     public  void delete$Message(){
        this.$Message.delete();
    }

     public  boolean addAll$Flags( int index,  Collection<? extends String> c){
        return this.$Flags.addAll(index,c);
    }

     public  boolean isEmpty$Flags(){
        return this.$Flags.isEmpty();
    }

     public  boolean removeIf$Flags( Predicate<? super String> filter){
        return this.$Flags.removeIf(filter);
    }

     public  void notify$Flags(){
        this.$Flags.notify();
    }

     public  boolean remove$Flags( Object o){
        return this.$Flags.remove(o);
    }

     public  String remove$Flags( int index){
        return this.$Flags.remove(index);
    }

     public  String get$Flags( int index){
        return this.$Flags.get(index);
    }

     public  int size$Flags(){
        return this.$Flags.size();
    }

     public  Stream<String> parallelStream$Flags(){
        return this.$Flags.parallelStream();
    }

     public  void notifyAll$Flags(){
        this.$Flags.notifyAll();
    }

     public  Iterator<String> iterator$Flags(){
        return this.$Flags.iterator();
    }

     public  void wait$Flags( long timeoutMillis,  int nanos)throws InterruptedException{
        this.$Flags.wait(timeoutMillis,nanos);
    }

     public  Optional<User> retrieveUser$Shard( long id){
        return this.$Shard.retrieveUser(id);
    }

     public  void clear$Flags(){
        this.$Flags.clear();
    }

     public  boolean removeAll$Flags( Collection<?> c){
        return this.$Flags.removeAll(c);
    }

     public  Optional<Guild> retrieveGuild$Shard( long id){
        return this.$Shard.retrieveGuild(id);
    }

     public  int indexOf$Flags( Object o){
        return this.$Flags.indexOf(o);
    }

     public  void add$Flags( int index,  String element){
        this.$Flags.add(index,element);
    }

     public  void forEach$Flags( Consumer<? super String> action){
        this.$Flags.forEach(action);
    }

     public  boolean add$Flags( String e){
        return this.$Flags.add(e);
    }

     public  boolean equals$Flags( Object obj){
        return this.$Flags.equals(obj);
    }

     public  void react$Message( String emote){
        this.$Message.react(emote);
    }

     public  SelfUser retrieveSelfUser$Shard(){
        return this.$Shard.retrieveSelfUser();
    }

     public  void set$Shard( Shard $Shard){
        this.$Shard = $Shard;
    }

}