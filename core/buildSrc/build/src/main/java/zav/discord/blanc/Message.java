
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
import java.lang.Runnable;
import java.lang.RuntimeException;
import java.lang.String;
import java.nio.file.Path;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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



 public  class Message   {
      private  long id ;
      private  Instant created ;
      private  User author ;
      private  Optional<String> content = java.util.Optional.empty();
      private  List<MessageEmbed> messageEmbeds = new java.util.concurrent.CopyOnWriteArrayList<>();
      private  List<Attachment> attachments = new java.util.concurrent.CopyOnWriteArrayList<>();
     public  void delete(){
        throw new UnsupportedOperationException();
    }

     public  void react( String emote){
        throw new UnsupportedOperationException();
    }

     public  void accept( ArchitectureVisitor visitor){

        visitor.handle(getRealThis());
    }

     public  Message getRealThis(){
        return this;
    }

     public  Stream<Attachment> streamAttachments(){
        return this.attachments.stream();
    }

     public  int hashCodeCreated(){
        return this.created.hashCode();
    }

     public  Instant plusMillisCreated( long millisToAdd){
        return this.created.plusMillis(millisToAdd);
    }

     public  MessageEmbed toMessageEmbedAuthor(){
        return this.author.toMessageEmbed();
    }

     public  boolean containsAllMessageEmbeds( Collection<?> c){
        return this.messageEmbeds.containsAll(c);
    }

     public  int hashCodeAttachments(){
        return this.attachments.hashCode();
    }

     public  void setContent( Optional<String> content){
        this.content = content;
    }

     public  String toStringContent(){
        return this.content.toString();
    }

     public  Object[] toArrayMessageEmbeds(){
        return this.messageEmbeds.toArray();
    }

     public  int hashCodeMessageEmbeds(){
        return this.messageEmbeds.hashCode();
    }

     public  void sortMessageEmbeds( Comparator<? super MessageEmbed> c){
        this.messageEmbeds.sort(c);
    }

     public  int sizeMessageEmbeds(){
        return this.messageEmbeds.size();
    }

     public  MessageEmbed getMessageEmbeds( int index){
        return this.messageEmbeds.get(index);
    }

     public  int hashCodeContent(){
        return this.content.hashCode();
    }

     public  Instant minusSecondsCreated( long secondsToSubtract){
        return this.created.minusSeconds(secondsToSubtract);
    }

     public  String orElseContent( String other){
        return this.content.orElse(other);
    }

     public  Object[] toArrayAttachments(){
        return this.attachments.toArray();
    }

     public  void waitContent( long timeoutMillis,  int nanos)throws InterruptedException{
        this.content.wait(timeoutMillis,nanos);
    }

     public  Instant withCreated( TemporalField field,  long newValue){
        return this.created.with(field,newValue);
    }

     public  Instant withCreated( TemporalAdjuster adjuster){
        return this.created.with(adjuster);
    }

     public  void waitAttachments()throws InterruptedException{
        this.attachments.wait();
    }

     public  void waitMessageEmbeds( long timeoutMillis,  int nanos)throws InterruptedException{
        this.messageEmbeds.wait(timeoutMillis,nanos);
    }

     public  int compareToCreated( Instant otherInstant){
        return this.created.compareTo(otherInstant);
    }

     public  int lastIndexOfMessageEmbeds( Object o){
        return this.messageEmbeds.lastIndexOf(o);
    }

     public  void forEachAttachments( Consumer<? super Attachment> action){
        this.attachments.forEach(action);
    }

     public  Attachment getAttachments( int index){
        return this.attachments.get(index);
    }

     public  boolean removeAllAttachments( Collection<?> c){
        return this.attachments.removeAll(c);
    }

     public  int indexOfAttachments( Object o){
        return this.attachments.indexOf(o);
    }

     public  void clearAttachments(){
        this.attachments.clear();
    }

     public  Optional<String> orContent( Supplier<? extends Optional<? extends String>> supplier){
        return this.content.or(supplier);
    }

     public  boolean addAllAttachments( Collection<? extends Attachment> c){
        return this.attachments.addAll(c);
    }

     public  OffsetDateTime atOffsetCreated( ZoneOffset offset){
        return this.created.atOffset(offset);
    }

     public  Instant minusCreated( long amountToSubtract,  TemporalUnit unit){
        return this.created.minus(amountToSubtract,unit);
    }

     public  boolean containsAllAttachments( Collection<?> c){
        return this.attachments.containsAll(c);
    }

     public  ListIterator<Attachment> listIteratorAttachments( int index){
        return this.attachments.listIterator(index);
    }

     public  void notifyAllCreated(){
        this.created.notifyAll();
    }

     public  void replaceAllAttachments( UnaryOperator<Attachment> operator){
        this.attachments.replaceAll(operator);
    }

     public  void waitAttachments( long timeoutMillis)throws InterruptedException{
        this.attachments.wait(timeoutMillis);
    }

     public  List<Attachment> getAttachments(){
        return this.attachments;
    }

     public  void ifPresentContent( Consumer<? super String> action){
        this.content.ifPresent(action);
    }

     public  ListIterator<Attachment> listIteratorAttachments(){
        return this.attachments.listIterator();
    }

     public  boolean isAfterCreated( Instant otherInstant){
        return this.created.isAfter(otherInstant);
    }

     public  void forEachMessageEmbeds( Consumer<? super MessageEmbed> action){
        this.messageEmbeds.forEach(action);
    }

     public  String orElseGetContent( Supplier<? extends String> supplier){
        return this.content.orElseGet(supplier);
    }

     public  Attachment removeAttachments( int index){
        return this.attachments.remove(index);
    }

     public  void waitAttachments( long timeoutMillis,  int nanos)throws InterruptedException{
        this.attachments.wait(timeoutMillis,nanos);
    }

     public  Instant minusMillisCreated( long millisToSubtract){
        return this.created.minusMillis(millisToSubtract);
    }

     public  void notifyAttachments(){
        this.attachments.notify();
    }

     public  ValueRange rangeCreated( TemporalField field){
        return this.created.range(field);
    }

     public  boolean containsAttachments( Object o){
        return this.attachments.contains(o);
    }

     public  Stream<String> streamContent(){
        return this.content.stream();
    }

     public  Stream<MessageEmbed> parallelStreamMessageEmbeds(){
        return this.messageEmbeds.parallelStream();
    }

     public  void notifyCreated(){
        this.created.notify();
    }

     public  ListIterator<MessageEmbed> listIteratorMessageEmbeds(){
        return this.messageEmbeds.listIterator();
    }

     public  MessageEmbed setMessageEmbeds( int index,  MessageEmbed element){
        return this.messageEmbeds.set(index,element);
    }

     public  ListIterator<MessageEmbed> listIteratorMessageEmbeds( int index){
        return this.messageEmbeds.listIterator(index);
    }

     public  void setAttachments( List<Attachment> attachments){
        this.attachments = attachments;
    }

     public  boolean isBeforeCreated( Instant otherInstant){
        return this.created.isBefore(otherInstant);
    }

     public  void waitMessageEmbeds()throws InterruptedException{
        this.messageEmbeds.wait();
    }

     public  Instant plusSecondsCreated( long secondsToAdd){
        return this.created.plusSeconds(secondsToAdd);
    }

     public  void setId( long id){
        this.id = id;
    }

     public  boolean isSupportedCreated( TemporalUnit unit){
        return this.created.isSupported(unit);
    }

     public  long untilCreated( Temporal endExclusive,  TemporalUnit unit){
        return this.created.until(endExclusive,unit);
    }

     public  boolean containsMessageEmbeds( Object o){
        return this.messageEmbeds.contains(o);
    }

     public  Attachment[] toArrayAttachments( IntFunction<Attachment[]> generator){
        return this.attachments.toArray(generator);
    }

     public  Temporal adjustIntoCreated( Temporal temporal){
        return this.created.adjustInto(temporal);
    }

     public  MessageEmbed[] toArrayMessageEmbeds( MessageEmbed[] a){
        return this.messageEmbeds.toArray(a);
    }

     public  boolean removeIfAttachments( Predicate<? super Attachment> filter){
        return this.attachments.removeIf(filter);
    }

     public  Instant truncatedToCreated( TemporalUnit unit){
        return this.created.truncatedTo(unit);
    }

     public  boolean equalsAttachments( Object obj){
        return this.attachments.equals(obj);
    }

     public  void notifyAllContent(){
        this.content.notifyAll();
    }

     public  int sizeAttachments(){
        return this.attachments.size();
    }

     public  boolean addMessageEmbeds( MessageEmbed e){
        return this.messageEmbeds.add(e);
    }

     public  int getNanoCreated(){
        return this.created.getNano();
    }

     public  void addAttachments( int index,  Attachment element){
        this.attachments.add(index,element);
    }

     public  void addMessageEmbeds( int index,  MessageEmbed element){
        this.messageEmbeds.add(index,element);
    }

     public  MessageEmbed removeMessageEmbeds( int index){
        return this.messageEmbeds.remove(index);
    }

     public  boolean addAttachments( Attachment e){
        return this.attachments.add(e);
    }

     public  Spliterator<MessageEmbed> spliteratorMessageEmbeds(){
        return this.messageEmbeds.spliterator();
    }

     public  Instant plusCreated( long amountToAdd,  TemporalUnit unit){
        return this.created.plus(amountToAdd,unit);
    }

     public  Attachment setAttachments( int index,  Attachment element){
        return this.attachments.set(index,element);
    }

     public  long toEpochMilliCreated(){
        return this.created.toEpochMilli();
    }

     public  boolean removeAllMessageEmbeds( Collection<?> c){
        return this.messageEmbeds.removeAll(c);
    }

     public  boolean retainAllAttachments( Collection<?> c){
        return this.attachments.retainAll(c);
    }

     public  boolean addAllAttachments( int index,  Collection<? extends Attachment> c){
        return this.attachments.addAll(index,c);
    }

     public  String toStringAttachments(){
        return this.attachments.toString();
    }

     public  boolean equalsCreated( Object otherInstant){
        return this.created.equals(otherInstant);
    }

     public  void waitContent()throws InterruptedException{
        this.content.wait();
    }

     public  String orElseThrowContent(){
        return this.content.orElseThrow();
    }

     public  boolean removeIfMessageEmbeds( Predicate<? super MessageEmbed> filter){
        return this.messageEmbeds.removeIf(filter);
    }

     public  void sortAttachments( Comparator<? super Attachment> c){
        this.attachments.sort(c);
    }

     public  List<MessageEmbed> getMessageEmbeds(){
        return this.messageEmbeds;
    }

     public  List<MessageEmbed> subListMessageEmbeds( int fromIndex,  int toIndex){
        return this.messageEmbeds.subList(fromIndex,toIndex);
    }

     public  int lastIndexOfAttachments( Object o){
        return this.attachments.lastIndexOf(o);
    }

     public  boolean isEmptyAttachments(){
        return this.attachments.isEmpty();
    }

     public  void setContent( String newValue){

        this.content = Optional.ofNullable(newValue);
    }

     public  Stream<MessageEmbed> streamMessageEmbeds(){
        return this.messageEmbeds.stream();
    }

     public  Instant plusNanosCreated( long nanosToAdd){
        return this.created.plusNanos(nanosToAdd);
    }

     public  MessageEmbed[] toArrayMessageEmbeds( IntFunction<MessageEmbed[]> generator){
        return this.messageEmbeds.toArray(generator);
    }

     public  long getEpochSecondCreated(){
        return this.created.getEpochSecond();
    }

     public  int indexOfMessageEmbeds( Object o){
        return this.messageEmbeds.indexOf(o);
    }

     public  void notifyAllAttachments(){
        this.attachments.notifyAll();
    }

     public  void setAuthor( User author){
        this.author = author;
    }

     public  List<Attachment> subListAttachments( int fromIndex,  int toIndex){
        return this.attachments.subList(fromIndex,toIndex);
    }

     public  Instant minusNanosCreated( long nanosToSubtract){
        return this.created.minusNanos(nanosToSubtract);
    }

     public  void acceptAuthor( ArchitectureVisitor visitor){
        this.author.accept(visitor);
    }

     public  String toStringMessageEmbeds(){
        return this.messageEmbeds.toString();
    }

     public  void waitContent( long timeoutMillis)throws InterruptedException{
        this.content.wait(timeoutMillis);
    }

     public  void waitCreated()throws InterruptedException{
        this.created.wait();
    }

     public  Instant minusCreated( TemporalAmount amountToSubtract){
        return this.created.minus(amountToSubtract);
    }

     public  Iterator<MessageEmbed> iteratorMessageEmbeds(){
        return this.messageEmbeds.iterator();
    }

     public  void notifyMessageEmbeds(){
        this.messageEmbeds.notify();
    }

     public  Instant plusCreated( TemporalAmount amountToAdd){
        return this.created.plus(amountToAdd);
    }

     public  User getAuthor(){
        return this.author;
    }

     public  boolean addAllMessageEmbeds( int index,  Collection<? extends MessageEmbed> c){
        return this.messageEmbeds.addAll(index,c);
    }

     public  void waitCreated( long timeoutMillis)throws InterruptedException{
        this.created.wait(timeoutMillis);
    }

     public  void replaceAllMessageEmbeds( UnaryOperator<MessageEmbed> operator){
        this.messageEmbeds.replaceAll(operator);
    }

     public  ZonedDateTime atZoneCreated( ZoneId zone){
        return this.created.atZone(zone);
    }

     public  void setCreated( Instant created){
        this.created = created;
    }

     public  boolean isEmptyContent(){
        return this.content.isEmpty();
    }

     public  void setMessageEmbeds( List<MessageEmbed> messageEmbeds){
        this.messageEmbeds = messageEmbeds;
    }

     public  long getId(){
        return this.id;
    }

     public  boolean isEmptyMessageEmbeds(){
        return this.messageEmbeds.isEmpty();
    }

     public  Attachment[] toArrayAttachments( Attachment[] a){
        return this.attachments.toArray(a);
    }

     public  PrivateChannel retrievePrivateChannelAuthor(){
        return this.author.retrievePrivateChannel();
    }

     public  void waitMessageEmbeds( long timeoutMillis)throws InterruptedException{
        this.messageEmbeds.wait(timeoutMillis);
    }

     public  String getAsMentionAuthor(){
        return this.author.getAsMention();
    }

     public  boolean equalsMessageEmbeds( Object obj){
        return this.messageEmbeds.equals(obj);
    }

     public  boolean retainAllMessageEmbeds( Collection<?> c){
        return this.messageEmbeds.retainAll(c);
    }

     public  boolean addAllMessageEmbeds( Collection<? extends MessageEmbed> c){
        return this.messageEmbeds.addAll(c);
    }

     public  int getCreated( TemporalField field){
        return this.created.get(field);
    }

     public  long getLongCreated( TemporalField field){
        return this.created.getLong(field);
    }

     public  Optional<String> filterContent( Predicate<? super String> predicate){
        return this.content.filter(predicate);
    }

     public  boolean isPresentContent(){
        return this.content.isPresent();
    }

     public  Iterator<Attachment> iteratorAttachments(){
        return this.attachments.iterator();
    }

     public  Optional<String> getContent(){
        return this.content;
    }

     public  boolean removeAttachments( Object o){
        return this.attachments.remove(o);
    }

     public  void clearMessageEmbeds(){
        this.messageEmbeds.clear();
    }

     public  void notifyContent(){
        this.content.notify();
    }

     public  User getRealThisAuthor(){
        return this.author.getRealThis();
    }

     public  String toStringCreated(){
        return this.created.toString();
    }

     public  boolean isSupportedCreated( TemporalField field){
        return this.created.isSupported(field);
    }

     public  Stream<Attachment> parallelStreamAttachments(){
        return this.attachments.parallelStream();
    }

     public  void ifPresentOrElseContent( Consumer<? super String> action,  Runnable emptyAction){
        this.content.ifPresentOrElse(action,emptyAction);
    }

     public  boolean equalsContent( Object obj){
        return this.content.equals(obj);
    }

     public  boolean removeMessageEmbeds( Object o){
        return this.messageEmbeds.remove(o);
    }

     public  void waitCreated( long timeoutMillis,  int nanos)throws InterruptedException{
        this.created.wait(timeoutMillis,nanos);
    }

     public  void notifyAllMessageEmbeds(){
        this.messageEmbeds.notifyAll();
    }

     public  Instant getCreated(){
        return this.created;
    }

     public  Spliterator<Attachment> spliteratorAttachments(){
        return this.attachments.spliterator();
    }

}