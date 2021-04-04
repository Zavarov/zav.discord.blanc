
package zav.discord.blanc;

import com.google.common.cache.Cache;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.CharSequence;
import java.lang.Class;
import java.lang.Comparable;
import java.lang.InterruptedException;
import java.lang.Object;
import java.lang.Runnable;
import java.lang.RuntimeException;
import java.lang.String;
import java.lang.StringBuffer;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import zav.discord.blanc._visitor.ArchitectureVisitor;
import zav.discord.blanc.activity.Activity;
import zav.jra.models.AbstractLink;
import zav.jra.models.AbstractSubreddit;



 public  class Author   {
      private  String name ;
      private  Optional<String> url = java.util.Optional.empty();
     public  void accept( ArchitectureVisitor visitor){

        visitor.handle(getRealThis());
    }

     public  Author getRealThis(){
        return this;
    }

     public  boolean equalsName( Object anObject){
        return this.name.equals(anObject);
    }

     public  String concatName( String str){
        return this.name.concat(str);
    }

     public  void ifPresentOrElseUrl( Consumer<? super String> action,  Runnable emptyAction){
        this.url.ifPresentOrElse(action,emptyAction);
    }

     public  int indexOfName( String str,  int fromIndex){
        return this.name.indexOf(str,fromIndex);
    }

     public  int lastIndexOfName( String str,  int fromIndex){
        return this.name.lastIndexOf(str,fromIndex);
    }

     public  void ifPresentUrl( Consumer<? super String> action){
        this.url.ifPresent(action);
    }

     public  String[] splitName( String regex){
        return this.name.split(regex);
    }

     public  void notifyAllName(){
        this.name.notifyAll();
    }

     public  int lastIndexOfName( int ch,  int fromIndex){
        return this.name.lastIndexOf(ch,fromIndex);
    }

     public  int codePointBeforeName( int index){
        return this.name.codePointBefore(index);
    }

     public  boolean regionMatchesName( int toffset,  String other,  int ooffset,  int len){
        return this.name.regionMatches(toffset,other,ooffset,len);
    }

     public  void getBytesName( int srcBegin,  int srcEnd,  byte[] dst,  int dstBegin){
        this.name.getBytes(srcBegin,srcEnd,dst,dstBegin);
    }

     public  Optional<String> getUrl(){
        return this.url;
    }

     public  boolean startsWithName( String prefix){
        return this.name.startsWith(prefix);
    }

     public  String toLowerCaseName(){
        return this.name.toLowerCase();
    }

     public  int indexOfName( String str){
        return this.name.indexOf(str);
    }

     public  int hashCodeUrl(){
        return this.url.hashCode();
    }

     public  String replaceName( CharSequence target,  CharSequence replacement){
        return this.name.replace(target,replacement);
    }

     public  String stripLeadingName(){
        return this.name.stripLeading();
    }

     public  String stripTrailingName(){
        return this.name.stripTrailing();
    }

     public  int lastIndexOfName( int ch){
        return this.name.lastIndexOf(ch);
    }

     public  void notifyAllUrl(){
        this.url.notifyAll();
    }

     public  String replaceFirstName( String regex,  String replacement){
        return this.name.replaceFirst(regex,replacement);
    }

     public  void waitName( long timeoutMillis,  int nanos)throws InterruptedException{
        this.name.wait(timeoutMillis,nanos);
    }

     public  void notifyName(){
        this.name.notify();
    }

     public  void waitUrl()throws InterruptedException{
        this.url.wait();
    }

     public  Optional<String> orUrl( Supplier<? extends Optional<? extends String>> supplier){
        return this.url.or(supplier);
    }

     public  int hashCodeName(){
        return this.name.hashCode();
    }

     public  String orElseUrl( String other){
        return this.url.orElse(other);
    }

     public  String toStringName(){
        return this.name.toString();
    }

     public  int compareToIgnoreCaseName( String str){
        return this.name.compareToIgnoreCase(str);
    }

     public  String toStringUrl(){
        return this.url.toString();
    }

     public  String orElseThrowUrl(){
        return this.url.orElseThrow();
    }

     public  int offsetByCodePointsName( int index,  int codePointOffset){
        return this.name.offsetByCodePoints(index,codePointOffset);
    }

     public  boolean isEmptyUrl(){
        return this.url.isEmpty();
    }

     public  int compareToName( String anotherString){
        return this.name.compareTo(anotherString);
    }

     public  boolean containsName( CharSequence s){
        return this.name.contains(s);
    }

     public  Stream<String> linesName(){
        return this.name.lines();
    }

     public  int indexOfName( int ch){
        return this.name.indexOf(ch);
    }

     public  void setUrl( String newValue){

        this.url = Optional.ofNullable(newValue);
    }

     public  boolean regionMatchesName( boolean ignoreCase,  int toffset,  String other,  int ooffset,  int len){
        return this.name.regionMatches(ignoreCase,toffset,other,ooffset,len);
    }

     public  char charAtName( int index){
        return this.name.charAt(index);
    }

     public  boolean contentEqualsName( CharSequence cs){
        return this.name.contentEquals(cs);
    }

     public  Stream<String> streamUrl(){
        return this.url.stream();
    }

     public  String trimName(){
        return this.name.trim();
    }

     public  String getName(){
        return this.name;
    }

     public  void setName( String name){
        this.name = name;
    }

     public  String orElseGetUrl( Supplier<? extends String> supplier){
        return this.url.orElseGet(supplier);
    }

     public  String replaceAllName( String regex,  String replacement){
        return this.name.replaceAll(regex,replacement);
    }

     public  CharSequence subSequenceName( int beginIndex,  int endIndex){
        return this.name.subSequence(beginIndex,endIndex);
    }

     public  int codePointAtName( int index){
        return this.name.codePointAt(index);
    }

     public  String toLowerCaseName( Locale locale){
        return this.name.toLowerCase(locale);
    }

     public  void waitName( long timeoutMillis)throws InterruptedException{
        this.name.wait(timeoutMillis);
    }

     public  char[] toCharArrayName(){
        return this.name.toCharArray();
    }

     public  IntStream codePointsName(){
        return this.name.codePoints();
    }

     public  int indexOfName( int ch,  int fromIndex){
        return this.name.indexOf(ch,fromIndex);
    }

     public  String stripName(){
        return this.name.strip();
    }

     public  String repeatName( int count){
        return this.name.repeat(count);
    }

     public  void waitName()throws InterruptedException{
        this.name.wait();
    }

     public  IntStream charsName(){
        return this.name.chars();
    }

     public  boolean startsWithName( String prefix,  int toffset){
        return this.name.startsWith(prefix,toffset);
    }

     public  void notifyUrl(){
        this.url.notify();
    }

     public  byte[] getBytesName(){
        return this.name.getBytes();
    }

     public  void setUrl( Optional<String> url){
        this.url = url;
    }

     public  boolean contentEqualsName( StringBuffer sb){
        return this.name.contentEquals(sb);
    }

     public  String[] splitName( String regex,  int limit){
        return this.name.split(regex,limit);
    }

     public  String replaceName( char oldChar,  char newChar){
        return this.name.replace(oldChar,newChar);
    }

     public  boolean equalsUrl( Object obj){
        return this.url.equals(obj);
    }

     public  void waitUrl( long timeoutMillis)throws InterruptedException{
        this.url.wait(timeoutMillis);
    }

     public  boolean isBlankName(){
        return this.name.isBlank();
    }

     public  void waitUrl( long timeoutMillis,  int nanos)throws InterruptedException{
        this.url.wait(timeoutMillis,nanos);
    }

     public  String substringName( int beginIndex,  int endIndex){
        return this.name.substring(beginIndex,endIndex);
    }

     public  boolean equalsIgnoreCaseName( String anotherString){
        return this.name.equalsIgnoreCase(anotherString);
    }

     public  String internName(){
        return this.name.intern();
    }

     public  boolean isEmptyName(){
        return this.name.isEmpty();
    }

     public  int lengthName(){
        return this.name.length();
    }

     public  int lastIndexOfName( String str){
        return this.name.lastIndexOf(str);
    }

     public  boolean matchesName( String regex){
        return this.name.matches(regex);
    }

     public  String substringName( int beginIndex){
        return this.name.substring(beginIndex);
    }

     public  int codePointCountName( int beginIndex,  int endIndex){
        return this.name.codePointCount(beginIndex,endIndex);
    }

     public  Optional<String> filterUrl( Predicate<? super String> predicate){
        return this.url.filter(predicate);
    }

     public  boolean endsWithName( String suffix){
        return this.name.endsWith(suffix);
    }

     public  boolean isPresentUrl(){
        return this.url.isPresent();
    }

     public  String toUpperCaseName(){
        return this.name.toUpperCase();
    }

     public  String toUpperCaseName( Locale locale){
        return this.name.toUpperCase(locale);
    }

}