
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
import zav.discord.blanc._visitor.ArchitectureVisitor;
import zav.discord.blanc.activity.Activity;
import zav.jra.models.AbstractLink;
import zav.jra.models.AbstractSubreddit;



 abstract  public  class MessageEmbedTOP   {
      private  Optional<Color> color = java.util.Optional.empty();
      private  Optional<String> thumbnail = java.util.Optional.empty();
      private  Optional<Title> title = java.util.Optional.empty();
      private  Optional<String> content = java.util.Optional.empty();
      private  Optional<Instant> timestamp = java.util.Optional.empty();
      private  Optional<Author> author = java.util.Optional.empty();
      private  List<Field> fields = new java.util.concurrent.CopyOnWriteArrayList<>();
     public  void accept( ArchitectureVisitor visitor){

        visitor.handle(getRealThis());
    }

     abstract  public  MessageEmbed getRealThis();

     public  Optional<Author> orAuthor( Supplier<? extends Optional<? extends Author>> supplier){
        return this.author.or(supplier);
    }

     public  Instant orElseGetTimestamp( Supplier<? extends Instant> supplier){
        return this.timestamp.orElseGet(supplier);
    }

     public  void setThumbnail( String newValue){

        this.thumbnail = Optional.ofNullable(newValue);
    }

     public  Optional<Instant> getTimestamp(){
        return this.timestamp;
    }

     public  void ifPresentOrElseColor( Consumer<? super Color> action,  Runnable emptyAction){
        this.color.ifPresentOrElse(action,emptyAction);
    }

     public  String toStringThumbnail(){
        return this.thumbnail.toString();
    }

     public  int hashCodeThumbnail(){
        return this.thumbnail.hashCode();
    }

     public  Author orElseGetAuthor( Supplier<? extends Author> supplier){
        return this.author.orElseGet(supplier);
    }

     public  Stream<Field> streamFields(){
        return this.fields.stream();
    }

     public  boolean isPresentContent(){
        return this.content.isPresent();
    }

     public  boolean equalsTitle( Object obj){
        return this.title.equals(obj);
    }

     public  boolean isEmptyAuthor(){
        return this.author.isEmpty();
    }

     public  void setColor( Color newValue){

        this.color = Optional.ofNullable(newValue);
    }

     public  boolean addFields( Field e){
        return this.fields.add(e);
    }

     public  String toStringFields(){
        return this.fields.toString();
    }

     public  void addFields( int index,  Field element){
        this.fields.add(index,element);
    }

     public  void waitAuthor( long timeoutMillis,  int nanos)throws InterruptedException{
        this.author.wait(timeoutMillis,nanos);
    }

     public  boolean isPresentColor(){
        return this.color.isPresent();
    }

     public  boolean isEmptyTimestamp(){
        return this.timestamp.isEmpty();
    }

     public  void setContent( String newValue){

        this.content = Optional.ofNullable(newValue);
    }

     public  Optional<Author> getAuthor(){
        return this.author;
    }

     public  Field[] toArrayFields( IntFunction<Field[]> generator){
        return this.fields.toArray(generator);
    }

     public  void notifyAllTitle(){
        this.title.notifyAll();
    }

     public  Optional<Author> filterAuthor( Predicate<? super Author> predicate){
        return this.author.filter(predicate);
    }

     public  String orElseGetContent( Supplier<? extends String> supplier){
        return this.content.orElseGet(supplier);
    }

     public  void waitColor()throws InterruptedException{
        this.color.wait();
    }

     public  Stream<Instant> streamTimestamp(){
        return this.timestamp.stream();
    }

     public  boolean removeAllFields( Collection<?> c){
        return this.fields.removeAll(c);
    }

     public  void waitAuthor( long timeoutMillis)throws InterruptedException{
        this.author.wait(timeoutMillis);
    }

     public  Optional<Color> filterColor( Predicate<? super Color> predicate){
        return this.color.filter(predicate);
    }

     public  void notifyTimestamp(){
        this.timestamp.notify();
    }

     public  boolean equalsTimestamp( Object obj){
        return this.timestamp.equals(obj);
    }

     public  void notifyThumbnail(){
        this.thumbnail.notify();
    }

     public  void setTimestamp( Optional<Instant> timestamp){
        this.timestamp = timestamp;
    }

     public  void ifPresentOrElseAuthor( Consumer<? super Author> action,  Runnable emptyAction){
        this.author.ifPresentOrElse(action,emptyAction);
    }

     public  Stream<String> streamThumbnail(){
        return this.thumbnail.stream();
    }

     public  boolean isPresentTimestamp(){
        return this.timestamp.isPresent();
    }

     public  Instant orElseTimestamp( Instant other){
        return this.timestamp.orElse(other);
    }

     public  String orElseThrowThumbnail(){
        return this.thumbnail.orElseThrow();
    }

     public  Object[] toArrayFields(){
        return this.fields.toArray();
    }

     public  void notifyAllContent(){
        this.content.notifyAll();
    }

     public  void ifPresentContent( Consumer<? super String> action){
        this.content.ifPresent(action);
    }

     public  int hashCodeAuthor(){
        return this.author.hashCode();
    }

     public  void notifyColor(){
        this.color.notify();
    }

     public  int hashCodeFields(){
        return this.fields.hashCode();
    }

     public  ListIterator<Field> listIteratorFields( int index){
        return this.fields.listIterator(index);
    }

     public  boolean equalsColor( Object obj){
        return this.color.equals(obj);
    }

     public  Optional<Title> getTitle(){
        return this.title;
    }

     public  Spliterator<Field> spliteratorFields(){
        return this.fields.spliterator();
    }

     public  void waitThumbnail( long timeoutMillis)throws InterruptedException{
        this.thumbnail.wait(timeoutMillis);
    }

     public  Iterator<Field> iteratorFields(){
        return this.fields.iterator();
    }

     public  Color orElseGetColor( Supplier<? extends Color> supplier){
        return this.color.orElseGet(supplier);
    }

     public  boolean containsAllFields( Collection<?> c){
        return this.fields.containsAll(c);
    }

     public  boolean addAllFields( int index,  Collection<? extends Field> c){
        return this.fields.addAll(index,c);
    }

     public  void waitTitle( long timeoutMillis)throws InterruptedException{
        this.title.wait(timeoutMillis);
    }

     public  String toStringContent(){
        return this.content.toString();
    }

     public  void waitContent( long timeoutMillis)throws InterruptedException{
        this.content.wait(timeoutMillis);
    }

     public  Title orElseThrowTitle(){
        return this.title.orElseThrow();
    }

     public  String toStringAuthor(){
        return this.author.toString();
    }

     public  Stream<Color> streamColor(){
        return this.color.stream();
    }

     public  void notifyAuthor(){
        this.author.notify();
    }

     public  String orElseThumbnail( String other){
        return this.thumbnail.orElse(other);
    }

     public  String toStringTitle(){
        return this.title.toString();
    }

     public  void waitTimestamp( long timeoutMillis)throws InterruptedException{
        this.timestamp.wait(timeoutMillis);
    }

     public  void waitThumbnail( long timeoutMillis,  int nanos)throws InterruptedException{
        this.thumbnail.wait(timeoutMillis,nanos);
    }

     public  void waitThumbnail()throws InterruptedException{
        this.thumbnail.wait();
    }

     public  Optional<Color> orColor( Supplier<? extends Optional<? extends Color>> supplier){
        return this.color.or(supplier);
    }

     public  Optional<Color> getColor(){
        return this.color;
    }

     public  Title orElseGetTitle( Supplier<? extends Title> supplier){
        return this.title.orElseGet(supplier);
    }

     public  void setTitle( Title newValue){

        this.title = Optional.ofNullable(newValue);
    }

     public  int hashCodeContent(){
        return this.content.hashCode();
    }

     public  Title orElseTitle( Title other){
        return this.title.orElse(other);
    }

     public  Optional<Title> filterTitle( Predicate<? super Title> predicate){
        return this.title.filter(predicate);
    }

     public  Stream<Title> streamTitle(){
        return this.title.stream();
    }

     public  boolean equalsThumbnail( Object obj){
        return this.thumbnail.equals(obj);
    }

     public  void setThumbnail( Optional<String> thumbnail){
        this.thumbnail = thumbnail;
    }

     public  void notifyAllAuthor(){
        this.author.notifyAll();
    }

     public  void notifyAllTimestamp(){
        this.timestamp.notifyAll();
    }

     public  void setAuthor( Author newValue){

        this.author = Optional.ofNullable(newValue);
    }

     public  void waitFields( long timeoutMillis)throws InterruptedException{
        this.fields.wait(timeoutMillis);
    }

     public  Stream<Author> streamAuthor(){
        return this.author.stream();
    }

     public  boolean isEmptyColor(){
        return this.color.isEmpty();
    }

     public  boolean removeIfFields( Predicate<? super Field> filter){
        return this.fields.removeIf(filter);
    }

     public  boolean equalsAuthor( Object obj){
        return this.author.equals(obj);
    }

     public  void clearFields(){
        this.fields.clear();
    }

     public  Instant orElseThrowTimestamp(){
        return this.timestamp.orElseThrow();
    }

     public  void waitColor( long timeoutMillis)throws InterruptedException{
        this.color.wait(timeoutMillis);
    }

     public  Optional<Instant> filterTimestamp( Predicate<? super Instant> predicate){
        return this.timestamp.filter(predicate);
    }

     public  int indexOfFields( Object o){
        return this.fields.indexOf(o);
    }

     public  boolean isEmptyTitle(){
        return this.title.isEmpty();
    }

     public  boolean retainAllFields( Collection<?> c){
        return this.fields.retainAll(c);
    }

     public  String orElseThrowContent(){
        return this.content.orElseThrow();
    }

     public  int lastIndexOfFields( Object o){
        return this.fields.lastIndexOf(o);
    }

     public  void setContent( Optional<String> content){
        this.content = content;
    }

     public  boolean isEmptyThumbnail(){
        return this.thumbnail.isEmpty();
    }

     public  Optional<Instant> orTimestamp( Supplier<? extends Optional<? extends Instant>> supplier){
        return this.timestamp.or(supplier);
    }

     public  boolean removeFields( Object o){
        return this.fields.remove(o);
    }

     public  boolean equalsContent( Object obj){
        return this.content.equals(obj);
    }

     public  void setFields( List<Field> fields){
        this.fields = fields;
    }

     public  boolean addAllFields( Collection<? extends Field> c){
        return this.fields.addAll(c);
    }

     public  Stream<Field> parallelStreamFields(){
        return this.fields.parallelStream();
    }

     public  Optional<String> filterThumbnail( Predicate<? super String> predicate){
        return this.thumbnail.filter(predicate);
    }

     public  Author orElseThrowAuthor(){
        return this.author.orElseThrow();
    }

     public  Field removeFields( int index){
        return this.fields.remove(index);
    }

     public  void setTitle( Optional<Title> title){
        this.title = title;
    }

     public  Optional<String> orContent( Supplier<? extends Optional<? extends String>> supplier){
        return this.content.or(supplier);
    }

     public  void waitTitle( long timeoutMillis,  int nanos)throws InterruptedException{
        this.title.wait(timeoutMillis,nanos);
    }

     public  Color orElseThrowColor(){
        return this.color.orElseThrow();
    }

     public  void waitAuthor()throws InterruptedException{
        this.author.wait();
    }

     public  void notifyContent(){
        this.content.notify();
    }

     public  void notifyTitle(){
        this.title.notify();
    }

     public  void replaceAllFields( UnaryOperator<Field> operator){
        this.fields.replaceAll(operator);
    }

     public  boolean isEmptyFields(){
        return this.fields.isEmpty();
    }

     public  void waitFields()throws InterruptedException{
        this.fields.wait();
    }

     public  void waitFields( long timeoutMillis,  int nanos)throws InterruptedException{
        this.fields.wait(timeoutMillis,nanos);
    }

     public  void waitTitle()throws InterruptedException{
        this.title.wait();
    }

     public  void ifPresentTitle( Consumer<? super Title> action){
        this.title.ifPresent(action);
    }

     public  boolean isEmptyContent(){
        return this.content.isEmpty();
    }

     public  Optional<String> orThumbnail( Supplier<? extends Optional<? extends String>> supplier){
        return this.thumbnail.or(supplier);
    }

     public  void sortFields( Comparator<? super Field> c){
        this.fields.sort(c);
    }

     public  void ifPresentThumbnail( Consumer<? super String> action){
        this.thumbnail.ifPresent(action);
    }

     public  boolean containsFields( Object o){
        return this.fields.contains(o);
    }

     public  void ifPresentOrElseThumbnail( Consumer<? super String> action,  Runnable emptyAction){
        this.thumbnail.ifPresentOrElse(action,emptyAction);
    }

     public  int hashCodeTitle(){
        return this.title.hashCode();
    }

     public  Field setFields( int index,  Field element){
        return this.fields.set(index,element);
    }

     public  Optional<String> filterContent( Predicate<? super String> predicate){
        return this.content.filter(predicate);
    }

     public  void setAuthor( Optional<Author> author){
        this.author = author;
    }

     public  void ifPresentOrElseTimestamp( Consumer<? super Instant> action,  Runnable emptyAction){
        this.timestamp.ifPresentOrElse(action,emptyAction);
    }

     public  int sizeFields(){
        return this.fields.size();
    }

     public  void notifyAllFields(){
        this.fields.notifyAll();
    }

     public  Author orElseAuthor( Author other){
        return this.author.orElse(other);
    }

     public  String orElseContent( String other){
        return this.content.orElse(other);
    }

     public  List<Field> subListFields( int fromIndex,  int toIndex){
        return this.fields.subList(fromIndex,toIndex);
    }

     public  List<Field> getFields(){
        return this.fields;
    }

     public  void ifPresentOrElseTitle( Consumer<? super Title> action,  Runnable emptyAction){
        this.title.ifPresentOrElse(action,emptyAction);
    }

     public  boolean isPresentThumbnail(){
        return this.thumbnail.isPresent();
    }

     public  int hashCodeTimestamp(){
        return this.timestamp.hashCode();
    }

     public  Optional<Title> orTitle( Supplier<? extends Optional<? extends Title>> supplier){
        return this.title.or(supplier);
    }

     public  String toStringTimestamp(){
        return this.timestamp.toString();
    }

     public  boolean isPresentAuthor(){
        return this.author.isPresent();
    }

     public  void ifPresentColor( Consumer<? super Color> action){
        this.color.ifPresent(action);
    }

     public  Stream<String> streamContent(){
        return this.content.stream();
    }

     public  void waitContent( long timeoutMillis,  int nanos)throws InterruptedException{
        this.content.wait(timeoutMillis,nanos);
    }

     public  void waitTimestamp( long timeoutMillis,  int nanos)throws InterruptedException{
        this.timestamp.wait(timeoutMillis,nanos);
    }

     public  void ifPresentOrElseContent( Consumer<? super String> action,  Runnable emptyAction){
        this.content.ifPresentOrElse(action,emptyAction);
    }

     public  void waitTimestamp()throws InterruptedException{
        this.timestamp.wait();
    }

     public  void ifPresentTimestamp( Consumer<? super Instant> action){
        this.timestamp.ifPresent(action);
    }

     public  Optional<String> getThumbnail(){
        return this.thumbnail;
    }

     public  Color orElseColor( Color other){
        return this.color.orElse(other);
    }

     public  void setTimestamp( Instant newValue){

        this.timestamp = Optional.ofNullable(newValue);
    }

     public  void notifyAllColor(){
        this.color.notifyAll();
    }

     public  String orElseGetThumbnail( Supplier<? extends String> supplier){
        return this.thumbnail.orElseGet(supplier);
    }

     public  void setColor( Optional<Color> color){
        this.color = color;
    }

     public  String toStringColor(){
        return this.color.toString();
    }

     public  void forEachFields( Consumer<? super Field> action){
        this.fields.forEach(action);
    }

     public  void waitColor( long timeoutMillis,  int nanos)throws InterruptedException{
        this.color.wait(timeoutMillis,nanos);
    }

     public  int hashCodeColor(){
        return this.color.hashCode();
    }

     public  void ifPresentAuthor( Consumer<? super Author> action){
        this.author.ifPresent(action);
    }

     public  ListIterator<Field> listIteratorFields(){
        return this.fields.listIterator();
    }

     public  Optional<String> getContent(){
        return this.content;
    }

     public  void notifyAllThumbnail(){
        this.thumbnail.notifyAll();
    }

     public  void notifyFields(){
        this.fields.notify();
    }

     public  Field[] toArrayFields( Field[] a){
        return this.fields.toArray(a);
    }

     public  boolean equalsFields( Object obj){
        return this.fields.equals(obj);
    }

     public  Field getFields( int index){
        return this.fields.get(index);
    }

     public  boolean isPresentTitle(){
        return this.title.isPresent();
    }

     public  void waitContent()throws InterruptedException{
        this.content.wait();
    }

}