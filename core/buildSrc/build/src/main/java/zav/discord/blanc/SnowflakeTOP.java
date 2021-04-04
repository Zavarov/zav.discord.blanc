
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
import java.util.stream.IntStream;
import java.util.stream.Stream;
import zav.discord.blanc._visitor.ArchitectureVisitor;
import zav.discord.blanc.activity.Activity;
import zav.jra.models.AbstractLink;
import zav.jra.models.AbstractSubreddit;



 abstract  public  class SnowflakeTOP   {
      private  long id ;
      private  String name ;
     public  void accept( ArchitectureVisitor visitor){

        visitor.handle(getRealThis());
    }

     abstract  public  Snowflake getRealThis();

     public  int indexOfName( String str,  int fromIndex){
        return this.name.indexOf(str,fromIndex);
    }

     public  void setName( String name){
        this.name = name;
    }

     public  Stream<String> linesName(){
        return this.name.lines();
    }

     public  int lastIndexOfName( String str,  int fromIndex){
        return this.name.lastIndexOf(str,fromIndex);
    }

     public  String toLowerCaseName( Locale locale){
        return this.name.toLowerCase(locale);
    }

     public  int codePointBeforeName( int index){
        return this.name.codePointBefore(index);
    }

     public  int compareToName( String anotherString){
        return this.name.compareTo(anotherString);
    }

     public  String replaceFirstName( String regex,  String replacement){
        return this.name.replaceFirst(regex,replacement);
    }

     public  boolean isEmptyName(){
        return this.name.isEmpty();
    }

     public  String internName(){
        return this.name.intern();
    }

     public  void getBytesName( int srcBegin,  int srcEnd,  byte[] dst,  int dstBegin){
        this.name.getBytes(srcBegin,srcEnd,dst,dstBegin);
    }

     public  String toUpperCaseName(){
        return this.name.toUpperCase();
    }

     public  byte[] getBytesName(){
        return this.name.getBytes();
    }

     public  CharSequence subSequenceName( int beginIndex,  int endIndex){
        return this.name.subSequence(beginIndex,endIndex);
    }

     public  IntStream charsName(){
        return this.name.chars();
    }

     public  void notifyAllName(){
        this.name.notifyAll();
    }

     public  String trimName(){
        return this.name.trim();
    }

     public  boolean equalsName( Object anObject){
        return this.name.equals(anObject);
    }

     public  int offsetByCodePointsName( int index,  int codePointOffset){
        return this.name.offsetByCodePoints(index,codePointOffset);
    }

     public  boolean regionMatchesName( int toffset,  String other,  int ooffset,  int len){
        return this.name.regionMatches(toffset,other,ooffset,len);
    }

     public  boolean isBlankName(){
        return this.name.isBlank();
    }

     public  int lastIndexOfName( int ch,  int fromIndex){
        return this.name.lastIndexOf(ch,fromIndex);
    }

     public  String[] splitName( String regex,  int limit){
        return this.name.split(regex,limit);
    }

     public  String toStringName(){
        return this.name.toString();
    }

     public  String[] splitName( String regex){
        return this.name.split(regex);
    }

     public  void waitName()throws InterruptedException{
        this.name.wait();
    }

     public  boolean equalsIgnoreCaseName( String anotherString){
        return this.name.equalsIgnoreCase(anotherString);
    }

     public  String toUpperCaseName( Locale locale){
        return this.name.toUpperCase(locale);
    }

     public  long getId(){
        return this.id;
    }

     public  void notifyName(){
        this.name.notify();
    }

     public  boolean contentEqualsName( StringBuffer sb){
        return this.name.contentEquals(sb);
    }

     public  String stripTrailingName(){
        return this.name.stripTrailing();
    }

     public  void waitName( long timeoutMillis,  int nanos)throws InterruptedException{
        this.name.wait(timeoutMillis,nanos);
    }

     public  String stripLeadingName(){
        return this.name.stripLeading();
    }

     public  int codePointAtName( int index){
        return this.name.codePointAt(index);
    }

     public  String replaceName( CharSequence target,  CharSequence replacement){
        return this.name.replace(target,replacement);
    }

     public  boolean startsWithName( String prefix){
        return this.name.startsWith(prefix);
    }

     public  void waitName( long timeoutMillis)throws InterruptedException{
        this.name.wait(timeoutMillis);
    }

     public  String substringName( int beginIndex,  int endIndex){
        return this.name.substring(beginIndex,endIndex);
    }

     public  boolean endsWithName( String suffix){
        return this.name.endsWith(suffix);
    }

     public  boolean regionMatchesName( boolean ignoreCase,  int toffset,  String other,  int ooffset,  int len){
        return this.name.regionMatches(ignoreCase,toffset,other,ooffset,len);
    }

     public  String substringName( int beginIndex){
        return this.name.substring(beginIndex);
    }

     public  char[] toCharArrayName(){
        return this.name.toCharArray();
    }

     public  int hashCodeName(){
        return this.name.hashCode();
    }

     public  boolean matchesName( String regex){
        return this.name.matches(regex);
    }

     public  String stripName(){
        return this.name.strip();
    }

     public  String replaceName( char oldChar,  char newChar){
        return this.name.replace(oldChar,newChar);
    }

     public  int codePointCountName( int beginIndex,  int endIndex){
        return this.name.codePointCount(beginIndex,endIndex);
    }

     public  int indexOfName( int ch){
        return this.name.indexOf(ch);
    }

     public  int lengthName(){
        return this.name.length();
    }

     public  String concatName( String str){
        return this.name.concat(str);
    }

     public  int indexOfName( int ch,  int fromIndex){
        return this.name.indexOf(ch,fromIndex);
    }

     public  IntStream codePointsName(){
        return this.name.codePoints();
    }

     public  boolean contentEqualsName( CharSequence cs){
        return this.name.contentEquals(cs);
    }

     public  String repeatName( int count){
        return this.name.repeat(count);
    }

     public  int indexOfName( String str){
        return this.name.indexOf(str);
    }

     public  int compareToIgnoreCaseName( String str){
        return this.name.compareToIgnoreCase(str);
    }

     public  String getName(){
        return this.name;
    }

     public  void setId( long id){
        this.id = id;
    }

     public  String toLowerCaseName(){
        return this.name.toLowerCase();
    }

     public  int lastIndexOfName( String str){
        return this.name.lastIndexOf(str);
    }

     public  int lastIndexOfName( int ch){
        return this.name.lastIndexOf(ch);
    }

     public  boolean startsWithName( String prefix,  int toffset){
        return this.name.startsWith(prefix,toffset);
    }

     public  char charAtName( int index){
        return this.name.charAt(index);
    }

     public  boolean containsName( CharSequence s){
        return this.name.contains(s);
    }

     public  String replaceAllName( String regex,  String replacement){
        return this.name.replaceAll(regex,replacement);
    }

}