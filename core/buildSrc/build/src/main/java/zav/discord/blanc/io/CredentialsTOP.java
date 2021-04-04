
package zav.discord.blanc.io;

import com.google.common.collect.Multimap;
import java.io.Serializable;
import java.lang.CharSequence;
import java.lang.Class;
import java.lang.Comparable;
import java.lang.InterruptedException;
import java.lang.Iterable;
import java.lang.Object;
import java.lang.String;
import java.lang.StringBuffer;
import java.nio.file.Path;
import java.nio.file.Watchable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import zav.discord.blanc.Rank;
import zav.discord.blanc.io._visitor.IOVisitor;



 abstract  public  class CredentialsTOP   {
      private  int statusMessageUpdateInterval ;
      private  int interactiveMessageLifetime ;
      private  int activityUpdateInterval ;
      private  String botName ;
      private  String globalPrefix ;
      private  int shardCount ;
      private  int imageWidth ;
      private  int imageHeight ;
      private  String inviteSupportServer ;
      private  String wikiUrl ;
      private  String discordToken ;
      private  String redditAccount ;
      private  String redditId ;
      private  String redditSecret ;
      private  Path jsonDirectory ;
     public  void accept( IOVisitor visitor){

        visitor.handle(getRealThis());
    }

     abstract  public  Credentials getRealThis();

     public  int indexOfWikiUrl( int ch){
        return this.wikiUrl.indexOf(ch);
    }

     public  String trimRedditSecret(){
        return this.redditSecret.trim();
    }

     public  void waitBotName()throws InterruptedException{
        this.botName.wait();
    }

     public  boolean regionMatchesInviteSupportServer( int toffset,  String other,  int ooffset,  int len){
        return this.inviteSupportServer.regionMatches(toffset,other,ooffset,len);
    }

     public  int indexOfDiscordToken( String str){
        return this.discordToken.indexOf(str);
    }

     public  String toStringWikiUrl(){
        return this.wikiUrl.toString();
    }

     public  String getBotName(){
        return this.botName;
    }

     public  String substringGlobalPrefix( int beginIndex){
        return this.globalPrefix.substring(beginIndex);
    }

     public  boolean startsWithBotName( String prefix){
        return this.botName.startsWith(prefix);
    }

     public  IntStream codePointsRedditId(){
        return this.redditId.codePoints();
    }

     public  int compareToJsonDirectory( Path other){
        return this.jsonDirectory.compareTo(other);
    }

     public  String substringDiscordToken( int beginIndex){
        return this.discordToken.substring(beginIndex);
    }

     public  int lastIndexOfDiscordToken( String str){
        return this.discordToken.lastIndexOf(str);
    }

     public  void notifyJsonDirectory(){
        this.jsonDirectory.notify();
    }

     public  boolean regionMatchesRedditId( boolean ignoreCase,  int toffset,  String other,  int ooffset,  int len){
        return this.redditId.regionMatches(ignoreCase,toffset,other,ooffset,len);
    }

     public  void setActivityUpdateInterval( int activityUpdateInterval){
        this.activityUpdateInterval = activityUpdateInterval;
    }

     public  boolean contentEqualsGlobalPrefix( CharSequence cs){
        return this.globalPrefix.contentEquals(cs);
    }

     public  boolean startsWithRedditId( String prefix){
        return this.redditId.startsWith(prefix);
    }

     public  int codePointCountRedditAccount( int beginIndex,  int endIndex){
        return this.redditAccount.codePointCount(beginIndex,endIndex);
    }

     public  int lastIndexOfRedditSecret( String str){
        return this.redditSecret.lastIndexOf(str);
    }

     public  int codePointCountBotName( int beginIndex,  int endIndex){
        return this.botName.codePointCount(beginIndex,endIndex);
    }

     public  boolean startsWithGlobalPrefix( String prefix,  int toffset){
        return this.globalPrefix.startsWith(prefix,toffset);
    }

     public  boolean equalsIgnoreCaseBotName( String anotherString){
        return this.botName.equalsIgnoreCase(anotherString);
    }

     public  void setShardCount( int shardCount){
        this.shardCount = shardCount;
    }

     public  int offsetByCodePointsDiscordToken( int index,  int codePointOffset){
        return this.discordToken.offsetByCodePoints(index,codePointOffset);
    }

     public  int indexOfGlobalPrefix( String str,  int fromIndex){
        return this.globalPrefix.indexOf(str,fromIndex);
    }

     public  int codePointCountGlobalPrefix( int beginIndex,  int endIndex){
        return this.globalPrefix.codePointCount(beginIndex,endIndex);
    }

     public  void notifyAllRedditId(){
        this.redditId.notifyAll();
    }

     public  String toLowerCaseDiscordToken( Locale locale){
        return this.discordToken.toLowerCase(locale);
    }

     public  Stream<String> linesBotName(){
        return this.botName.lines();
    }

     public  String[] splitDiscordToken( String regex,  int limit){
        return this.discordToken.split(regex,limit);
    }

     public  void waitRedditAccount( long timeoutMillis)throws InterruptedException{
        this.redditAccount.wait(timeoutMillis);
    }

     public  boolean equalsInviteSupportServer( Object anObject){
        return this.inviteSupportServer.equals(anObject);
    }

     public  char charAtRedditId( int index){
        return this.redditId.charAt(index);
    }

     public  Path getFileNameJsonDirectory(){
        return this.jsonDirectory.getFileName();
    }

     public  String getRedditSecret(){
        return this.redditSecret;
    }

     public  boolean contentEqualsInviteSupportServer( CharSequence cs){
        return this.inviteSupportServer.contentEquals(cs);
    }

     public  CharSequence subSequenceRedditSecret( int beginIndex,  int endIndex){
        return this.redditSecret.subSequence(beginIndex,endIndex);
    }

     public  boolean endsWithInviteSupportServer( String suffix){
        return this.inviteSupportServer.endsWith(suffix);
    }

     public  void setStatusMessageUpdateInterval( int statusMessageUpdateInterval){
        this.statusMessageUpdateInterval = statusMessageUpdateInterval;
    }

     public  String substringRedditId( int beginIndex){
        return this.redditId.substring(beginIndex);
    }

     public  boolean isEmptyDiscordToken(){
        return this.discordToken.isEmpty();
    }

     public  int lastIndexOfDiscordToken( String str,  int fromIndex){
        return this.discordToken.lastIndexOf(str,fromIndex);
    }

     public  String replaceAllRedditSecret( String regex,  String replacement){
        return this.redditSecret.replaceAll(regex,replacement);
    }

     public  char charAtRedditAccount( int index){
        return this.redditAccount.charAt(index);
    }

     public  boolean isBlankBotName(){
        return this.botName.isBlank();
    }

     public  int lastIndexOfWikiUrl( int ch,  int fromIndex){
        return this.wikiUrl.lastIndexOf(ch,fromIndex);
    }

     public  Path getRootJsonDirectory(){
        return this.jsonDirectory.getRoot();
    }

     public  boolean equalsRedditSecret( Object anObject){
        return this.redditSecret.equals(anObject);
    }

     public  void getBytesRedditAccount( int srcBegin,  int srcEnd,  byte[] dst,  int dstBegin){
        this.redditAccount.getBytes(srcBegin,srcEnd,dst,dstBegin);
    }

     public  boolean isBlankInviteSupportServer(){
        return this.inviteSupportServer.isBlank();
    }

     public  boolean isBlankGlobalPrefix(){
        return this.globalPrefix.isBlank();
    }

     public  int hashCodeInviteSupportServer(){
        return this.inviteSupportServer.hashCode();
    }

     public  String toLowerCaseRedditSecret(){
        return this.redditSecret.toLowerCase();
    }

     public  String internWikiUrl(){
        return this.wikiUrl.intern();
    }

     public  boolean isEmptyGlobalPrefix(){
        return this.globalPrefix.isEmpty();
    }

     public  String[] splitRedditId( String regex){
        return this.redditId.split(regex);
    }

     public  String toStringRedditAccount(){
        return this.redditAccount.toString();
    }

     public  CharSequence subSequenceRedditAccount( int beginIndex,  int endIndex){
        return this.redditAccount.subSequence(beginIndex,endIndex);
    }

     public  String stripLeadingInviteSupportServer(){
        return this.inviteSupportServer.stripLeading();
    }

     public  int indexOfBotName( String str){
        return this.botName.indexOf(str);
    }

     public  String toStringRedditSecret(){
        return this.redditSecret.toString();
    }

     public  String concatBotName( String str){
        return this.botName.concat(str);
    }

     public  boolean matchesInviteSupportServer( String regex){
        return this.inviteSupportServer.matches(regex);
    }

     public  void forEachJsonDirectory( Consumer<? super Path> action){
        this.jsonDirectory.forEach(action);
    }

     public  boolean equalsIgnoreCaseRedditId( String anotherString){
        return this.redditId.equalsIgnoreCase(anotherString);
    }

     public  boolean regionMatchesGlobalPrefix( boolean ignoreCase,  int toffset,  String other,  int ooffset,  int len){
        return this.globalPrefix.regionMatches(ignoreCase,toffset,other,ooffset,len);
    }

     public  String toLowerCaseDiscordToken(){
        return this.discordToken.toLowerCase();
    }

     public  int codePointAtRedditAccount( int index){
        return this.redditAccount.codePointAt(index);
    }

     public  int lengthGlobalPrefix(){
        return this.globalPrefix.length();
    }

     public  String[] splitWikiUrl( String regex,  int limit){
        return this.wikiUrl.split(regex,limit);
    }

     public  String toUpperCaseInviteSupportServer(){
        return this.inviteSupportServer.toUpperCase();
    }

     public  String getWikiUrl(){
        return this.wikiUrl;
    }

     public  int indexOfWikiUrl( String str){
        return this.wikiUrl.indexOf(str);
    }

     public  int lengthInviteSupportServer(){
        return this.inviteSupportServer.length();
    }

     public  void notifyAllGlobalPrefix(){
        this.globalPrefix.notifyAll();
    }

     public  void notifyRedditId(){
        this.redditId.notify();
    }

     public  int codePointBeforeWikiUrl( int index){
        return this.wikiUrl.codePointBefore(index);
    }

     public  String replaceGlobalPrefix( CharSequence target,  CharSequence replacement){
        return this.globalPrefix.replace(target,replacement);
    }

     public  boolean startsWithWikiUrl( String prefix,  int toffset){
        return this.wikiUrl.startsWith(prefix,toffset);
    }

     public  int indexOfDiscordToken( int ch,  int fromIndex){
        return this.discordToken.indexOf(ch,fromIndex);
    }

     public  void notifyRedditAccount(){
        this.redditAccount.notify();
    }

     public  int indexOfRedditAccount( String str){
        return this.redditAccount.indexOf(str);
    }

     public  boolean isBlankRedditId(){
        return this.redditId.isBlank();
    }

     public  int getShardCount(){
        return this.shardCount;
    }

     public  int indexOfBotName( int ch,  int fromIndex){
        return this.botName.indexOf(ch,fromIndex);
    }

     public  void waitDiscordToken( long timeoutMillis,  int nanos)throws InterruptedException{
        this.discordToken.wait(timeoutMillis,nanos);
    }

     public  Stream<String> linesWikiUrl(){
        return this.wikiUrl.lines();
    }

     public  String concatRedditAccount( String str){
        return this.redditAccount.concat(str);
    }

     public  Path normalizeJsonDirectory(){
        return this.jsonDirectory.normalize();
    }

     public  String toLowerCaseRedditAccount(){
        return this.redditAccount.toLowerCase();
    }

     public  int getActivityUpdateInterval(){
        return this.activityUpdateInterval;
    }

     public  boolean isAbsoluteJsonDirectory(){
        return this.jsonDirectory.isAbsolute();
    }

     public  int codePointBeforeDiscordToken( int index){
        return this.discordToken.codePointBefore(index);
    }

     public  IntStream codePointsWikiUrl(){
        return this.wikiUrl.codePoints();
    }

     public  String repeatRedditId( int count){
        return this.redditId.repeat(count);
    }

     public  int indexOfRedditId( String str){
        return this.redditId.indexOf(str);
    }

     public  String replaceFirstRedditId( String regex,  String replacement){
        return this.redditId.replaceFirst(regex,replacement);
    }

     public  int lengthDiscordToken(){
        return this.discordToken.length();
    }

     public  int lastIndexOfBotName( int ch){
        return this.botName.lastIndexOf(ch);
    }

     public  String substringBotName( int beginIndex,  int endIndex){
        return this.botName.substring(beginIndex,endIndex);
    }

     public  String toLowerCaseRedditId( Locale locale){
        return this.redditId.toLowerCase(locale);
    }

     public  byte[] getBytesDiscordToken(){
        return this.discordToken.getBytes();
    }

     public  boolean regionMatchesRedditSecret( boolean ignoreCase,  int toffset,  String other,  int ooffset,  int len){
        return this.redditSecret.regionMatches(ignoreCase,toffset,other,ooffset,len);
    }

     public  String toLowerCaseInviteSupportServer( Locale locale){
        return this.inviteSupportServer.toLowerCase(locale);
    }

     public  String toUpperCaseRedditAccount( Locale locale){
        return this.redditAccount.toUpperCase(locale);
    }

     public  String stripTrailingDiscordToken(){
        return this.discordToken.stripTrailing();
    }

     public  String[] splitBotName( String regex,  int limit){
        return this.botName.split(regex,limit);
    }

     public  int compareToGlobalPrefix( String anotherString){
        return this.globalPrefix.compareTo(anotherString);
    }

     public  String toUpperCaseDiscordToken(){
        return this.discordToken.toUpperCase();
    }

     public  int codePointAtBotName( int index){
        return this.botName.codePointAt(index);
    }

     public  String replaceFirstDiscordToken( String regex,  String replacement){
        return this.discordToken.replaceFirst(regex,replacement);
    }

     public  void waitRedditAccount()throws InterruptedException{
        this.redditAccount.wait();
    }

     public  void waitWikiUrl()throws InterruptedException{
        this.wikiUrl.wait();
    }

     public  IntStream charsRedditId(){
        return this.redditId.chars();
    }

     public  String internBotName(){
        return this.botName.intern();
    }

     public  int lastIndexOfRedditId( int ch,  int fromIndex){
        return this.redditId.lastIndexOf(ch,fromIndex);
    }

     public  int offsetByCodePointsRedditAccount( int index,  int codePointOffset){
        return this.redditAccount.offsetByCodePoints(index,codePointOffset);
    }

     public  boolean startsWithRedditId( String prefix,  int toffset){
        return this.redditId.startsWith(prefix,toffset);
    }

     public  int indexOfRedditAccount( int ch,  int fromIndex){
        return this.redditAccount.indexOf(ch,fromIndex);
    }

     public  String[] splitGlobalPrefix( String regex,  int limit){
        return this.globalPrefix.split(regex,limit);
    }

     public  int compareToWikiUrl( String anotherString){
        return this.wikiUrl.compareTo(anotherString);
    }

     public  int indexOfGlobalPrefix( String str){
        return this.globalPrefix.indexOf(str);
    }

     public  String toStringInviteSupportServer(){
        return this.inviteSupportServer.toString();
    }

     public  void waitRedditSecret( long timeoutMillis)throws InterruptedException{
        this.redditSecret.wait(timeoutMillis);
    }

     public  int lengthWikiUrl(){
        return this.wikiUrl.length();
    }

     public  IntStream charsInviteSupportServer(){
        return this.inviteSupportServer.chars();
    }

     public  boolean startsWithDiscordToken( String prefix){
        return this.discordToken.startsWith(prefix);
    }

     public  int hashCodeWikiUrl(){
        return this.wikiUrl.hashCode();
    }

     public  boolean equalsIgnoreCaseGlobalPrefix( String anotherString){
        return this.globalPrefix.equalsIgnoreCase(anotherString);
    }

     public  String substringRedditAccount( int beginIndex){
        return this.redditAccount.substring(beginIndex);
    }

     public  int compareToBotName( String anotherString){
        return this.botName.compareTo(anotherString);
    }

     public  String replaceAllDiscordToken( String regex,  String replacement){
        return this.discordToken.replaceAll(regex,replacement);
    }

     public  String substringRedditSecret( int beginIndex){
        return this.redditSecret.substring(beginIndex);
    }

     public  byte[] getBytesRedditId(){
        return this.redditId.getBytes();
    }

     public  int lastIndexOfBotName( int ch,  int fromIndex){
        return this.botName.lastIndexOf(ch,fromIndex);
    }

     public  Stream<String> linesDiscordToken(){
        return this.discordToken.lines();
    }

     public  String replaceAllBotName( String regex,  String replacement){
        return this.botName.replaceAll(regex,replacement);
    }

     public  Stream<String> linesRedditSecret(){
        return this.redditSecret.lines();
    }

     public  int compareToIgnoreCaseRedditAccount( String str){
        return this.redditAccount.compareToIgnoreCase(str);
    }

     public  boolean equalsBotName( Object anObject){
        return this.botName.equals(anObject);
    }

     public  void waitWikiUrl( long timeoutMillis)throws InterruptedException{
        this.wikiUrl.wait(timeoutMillis);
    }

     public  String toUpperCaseRedditSecret( Locale locale){
        return this.redditSecret.toUpperCase(locale);
    }

     public  Stream<String> linesGlobalPrefix(){
        return this.globalPrefix.lines();
    }

     public  int offsetByCodePointsRedditSecret( int index,  int codePointOffset){
        return this.redditSecret.offsetByCodePoints(index,codePointOffset);
    }

     public  boolean regionMatchesWikiUrl( boolean ignoreCase,  int toffset,  String other,  int ooffset,  int len){
        return this.wikiUrl.regionMatches(ignoreCase,toffset,other,ooffset,len);
    }

     public  String concatRedditSecret( String str){
        return this.redditSecret.concat(str);
    }

     public  String getRedditId(){
        return this.redditId;
    }

     public  CharSequence subSequenceWikiUrl( int beginIndex,  int endIndex){
        return this.wikiUrl.subSequence(beginIndex,endIndex);
    }

     public  int lastIndexOfGlobalPrefix( String str){
        return this.globalPrefix.lastIndexOf(str);
    }

     public  String internRedditId(){
        return this.redditId.intern();
    }

     public  String toLowerCaseInviteSupportServer(){
        return this.inviteSupportServer.toLowerCase();
    }

     public  int indexOfWikiUrl( int ch,  int fromIndex){
        return this.wikiUrl.indexOf(ch,fromIndex);
    }

     public  String replaceFirstInviteSupportServer( String regex,  String replacement){
        return this.inviteSupportServer.replaceFirst(regex,replacement);
    }

     public  CharSequence subSequenceGlobalPrefix( int beginIndex,  int endIndex){
        return this.globalPrefix.subSequence(beginIndex,endIndex);
    }

     public  String toLowerCaseWikiUrl(){
        return this.wikiUrl.toLowerCase();
    }

     public  int codePointBeforeGlobalPrefix( int index){
        return this.globalPrefix.codePointBefore(index);
    }

     public  int hashCodeRedditAccount(){
        return this.redditAccount.hashCode();
    }

     public  boolean equalsRedditAccount( Object anObject){
        return this.redditAccount.equals(anObject);
    }

     public  int getImageHeight(){
        return this.imageHeight;
    }

     public  String replaceInviteSupportServer( CharSequence target,  CharSequence replacement){
        return this.inviteSupportServer.replace(target,replacement);
    }

     public  boolean matchesWikiUrl( String regex){
        return this.wikiUrl.matches(regex);
    }

     public  Path subpathJsonDirectory( int beginIndex,  int endIndex){
        return this.jsonDirectory.subpath(beginIndex,endIndex);
    }

     public  int compareToRedditSecret( String anotherString){
        return this.redditSecret.compareTo(anotherString);
    }

     public  void notifyDiscordToken(){
        this.discordToken.notify();
    }

     public  String replaceAllGlobalPrefix( String regex,  String replacement){
        return this.globalPrefix.replaceAll(regex,replacement);
    }

     public  Path resolveSiblingJsonDirectory( String other){
        return this.jsonDirectory.resolveSibling(other);
    }

     public  String stripTrailingBotName(){
        return this.botName.stripTrailing();
    }

     public  boolean equalsGlobalPrefix( Object anObject){
        return this.globalPrefix.equals(anObject);
    }

     public  void getBytesGlobalPrefix( int srcBegin,  int srcEnd,  byte[] dst,  int dstBegin){
        this.globalPrefix.getBytes(srcBegin,srcEnd,dst,dstBegin);
    }

     public  int lastIndexOfRedditSecret( int ch){
        return this.redditSecret.lastIndexOf(ch);
    }

     public  IntStream charsWikiUrl(){
        return this.wikiUrl.chars();
    }

     public  boolean contentEqualsInviteSupportServer( StringBuffer sb){
        return this.inviteSupportServer.contentEquals(sb);
    }

     public  String toUpperCaseWikiUrl( Locale locale){
        return this.wikiUrl.toUpperCase(locale);
    }

     public  String trimInviteSupportServer(){
        return this.inviteSupportServer.trim();
    }

     public  int lastIndexOfRedditSecret( int ch,  int fromIndex){
        return this.redditSecret.lastIndexOf(ch,fromIndex);
    }

     public  String getInviteSupportServer(){
        return this.inviteSupportServer;
    }

     public  int lastIndexOfDiscordToken( int ch,  int fromIndex){
        return this.discordToken.lastIndexOf(ch,fromIndex);
    }

     public  String replaceRedditAccount( char oldChar,  char newChar){
        return this.redditAccount.replace(oldChar,newChar);
    }

     public  int codePointAtWikiUrl( int index){
        return this.wikiUrl.codePointAt(index);
    }

     public  int lastIndexOfInviteSupportServer( String str,  int fromIndex){
        return this.inviteSupportServer.lastIndexOf(str,fromIndex);
    }

     public  String repeatDiscordToken( int count){
        return this.discordToken.repeat(count);
    }

     public  boolean equalsDiscordToken( Object anObject){
        return this.discordToken.equals(anObject);
    }

     public  int lastIndexOfWikiUrl( int ch){
        return this.wikiUrl.lastIndexOf(ch);
    }

     public  Path toAbsolutePathJsonDirectory(){
        return this.jsonDirectory.toAbsolutePath();
    }

     public  String toUpperCaseRedditId( Locale locale){
        return this.redditId.toUpperCase(locale);
    }

     public  int lengthRedditSecret(){
        return this.redditSecret.length();
    }

     public  Path resolveSiblingJsonDirectory( Path other){
        return this.jsonDirectory.resolveSibling(other);
    }

     public  String replaceRedditSecret( CharSequence target,  CharSequence replacement){
        return this.redditSecret.replace(target,replacement);
    }

     public  int lastIndexOfRedditId( String str){
        return this.redditId.lastIndexOf(str);
    }

     public  int getImageWidth(){
        return this.imageWidth;
    }

     public  void setRedditSecret( String redditSecret){
        this.redditSecret = redditSecret;
    }

     public  String toStringBotName(){
        return this.botName.toString();
    }

     public  String replaceBotName( CharSequence target,  CharSequence replacement){
        return this.botName.replace(target,replacement);
    }

     public  String stripTrailingInviteSupportServer(){
        return this.inviteSupportServer.stripTrailing();
    }

     public  String substringWikiUrl( int beginIndex,  int endIndex){
        return this.wikiUrl.substring(beginIndex,endIndex);
    }

     public  int indexOfInviteSupportServer( int ch,  int fromIndex){
        return this.inviteSupportServer.indexOf(ch,fromIndex);
    }

     public  String[] splitRedditId( String regex,  int limit){
        return this.redditId.split(regex,limit);
    }

     public  boolean isEmptyInviteSupportServer(){
        return this.inviteSupportServer.isEmpty();
    }

     public  int indexOfRedditAccount( int ch){
        return this.redditAccount.indexOf(ch);
    }

     public  String replaceAllInviteSupportServer( String regex,  String replacement){
        return this.inviteSupportServer.replaceAll(regex,replacement);
    }

     public  void setJsonDirectory( Path jsonDirectory){
        this.jsonDirectory = jsonDirectory;
    }

     public  boolean containsGlobalPrefix( CharSequence s){
        return this.globalPrefix.contains(s);
    }

     public  String concatInviteSupportServer( String str){
        return this.inviteSupportServer.concat(str);
    }

     public  boolean endsWithJsonDirectory( Path other){
        return this.jsonDirectory.endsWith(other);
    }

     public  String toUpperCaseRedditId(){
        return this.redditId.toUpperCase();
    }

     public  int codePointCountRedditId( int beginIndex,  int endIndex){
        return this.redditId.codePointCount(beginIndex,endIndex);
    }

     public  String replaceBotName( char oldChar,  char newChar){
        return this.botName.replace(oldChar,newChar);
    }

     public  int codePointBeforeRedditId( int index){
        return this.redditId.codePointBefore(index);
    }

     public  boolean startsWithRedditSecret( String prefix){
        return this.redditSecret.startsWith(prefix);
    }

     public  String substringRedditAccount( int beginIndex,  int endIndex){
        return this.redditAccount.substring(beginIndex,endIndex);
    }

     public  void notifyAllDiscordToken(){
        this.discordToken.notifyAll();
    }

     public  int lastIndexOfRedditId( String str,  int fromIndex){
        return this.redditId.lastIndexOf(str,fromIndex);
    }

     public  String trimGlobalPrefix(){
        return this.globalPrefix.trim();
    }

     public  boolean isBlankRedditSecret(){
        return this.redditSecret.isBlank();
    }

     public  void waitRedditAccount( long timeoutMillis,  int nanos)throws InterruptedException{
        this.redditAccount.wait(timeoutMillis,nanos);
    }

     public  boolean isEmptyRedditId(){
        return this.redditId.isEmpty();
    }

     public  void waitBotName( long timeoutMillis)throws InterruptedException{
        this.botName.wait(timeoutMillis);
    }

     public  Spliterator<Path> spliteratorJsonDirectory(){
        return this.jsonDirectory.spliterator();
    }

     public  boolean contentEqualsRedditAccount( CharSequence cs){
        return this.redditAccount.contentEquals(cs);
    }

     public  boolean endsWithJsonDirectory( String other){
        return this.jsonDirectory.endsWith(other);
    }

     public  String toLowerCaseGlobalPrefix(){
        return this.globalPrefix.toLowerCase();
    }

     public  String stripLeadingDiscordToken(){
        return this.discordToken.stripLeading();
    }

     public  int lastIndexOfDiscordToken( int ch){
        return this.discordToken.lastIndexOf(ch);
    }

     public  boolean contentEqualsWikiUrl( CharSequence cs){
        return this.wikiUrl.contentEquals(cs);
    }

     public  String toUpperCaseDiscordToken( Locale locale){
        return this.discordToken.toUpperCase(locale);
    }

     public  byte[] getBytesBotName(){
        return this.botName.getBytes();
    }

     public  String substringGlobalPrefix( int beginIndex,  int endIndex){
        return this.globalPrefix.substring(beginIndex,endIndex);
    }

     public  String replaceAllRedditId( String regex,  String replacement){
        return this.redditId.replaceAll(regex,replacement);
    }

     public  String replaceInviteSupportServer( char oldChar,  char newChar){
        return this.inviteSupportServer.replace(oldChar,newChar);
    }

     public  byte[] getBytesRedditSecret(){
        return this.redditSecret.getBytes();
    }

     public  void setInviteSupportServer( String inviteSupportServer){
        this.inviteSupportServer = inviteSupportServer;
    }

     public  int lastIndexOfRedditSecret( String str,  int fromIndex){
        return this.redditSecret.lastIndexOf(str,fromIndex);
    }

     public  Path getJsonDirectory(){
        return this.jsonDirectory;
    }

     public  int indexOfInviteSupportServer( String str){
        return this.inviteSupportServer.indexOf(str);
    }

     public  int lastIndexOfBotName( String str,  int fromIndex){
        return this.botName.lastIndexOf(str,fromIndex);
    }

     public  String replaceDiscordToken( char oldChar,  char newChar){
        return this.discordToken.replace(oldChar,newChar);
    }

     public  char charAtRedditSecret( int index){
        return this.redditSecret.charAt(index);
    }

     public  boolean containsRedditId( CharSequence s){
        return this.redditId.contains(s);
    }

     public  int hashCodeRedditSecret(){
        return this.redditSecret.hashCode();
    }

     public  Stream<String> linesInviteSupportServer(){
        return this.inviteSupportServer.lines();
    }

     public  String[] splitGlobalPrefix( String regex){
        return this.globalPrefix.split(regex);
    }

     public  boolean equalsJsonDirectory( Object other){
        return this.jsonDirectory.equals(other);
    }

     public  boolean equalsIgnoreCaseRedditSecret( String anotherString){
        return this.redditSecret.equalsIgnoreCase(anotherString);
    }

     public  boolean matchesRedditId( String regex){
        return this.redditId.matches(regex);
    }

     public  boolean equalsIgnoreCaseInviteSupportServer( String anotherString){
        return this.inviteSupportServer.equalsIgnoreCase(anotherString);
    }

     public  boolean matchesRedditSecret( String regex){
        return this.redditSecret.matches(regex);
    }

     public  int indexOfBotName( String str,  int fromIndex){
        return this.botName.indexOf(str,fromIndex);
    }

     public  String stripBotName(){
        return this.botName.strip();
    }

     public  CharSequence subSequenceRedditId( int beginIndex,  int endIndex){
        return this.redditId.subSequence(beginIndex,endIndex);
    }

     public  char[] toCharArrayInviteSupportServer(){
        return this.inviteSupportServer.toCharArray();
    }

     public  int lengthRedditId(){
        return this.redditId.length();
    }

     public  boolean regionMatchesWikiUrl( int toffset,  String other,  int ooffset,  int len){
        return this.wikiUrl.regionMatches(toffset,other,ooffset,len);
    }

     public  int indexOfInviteSupportServer( int ch){
        return this.inviteSupportServer.indexOf(ch);
    }

     public  int compareToRedditAccount( String anotherString){
        return this.redditAccount.compareTo(anotherString);
    }

     public  int indexOfRedditSecret( String str){
        return this.redditSecret.indexOf(str);
    }

     public  void notifyGlobalPrefix(){
        this.globalPrefix.notify();
    }

     public  void waitRedditId( long timeoutMillis)throws InterruptedException{
        this.redditId.wait(timeoutMillis);
    }

     public  String toUpperCaseGlobalPrefix(){
        return this.globalPrefix.toUpperCase();
    }

     public  int indexOfRedditAccount( String str,  int fromIndex){
        return this.redditAccount.indexOf(str,fromIndex);
    }

     public  int lengthRedditAccount(){
        return this.redditAccount.length();
    }

     public  int compareToIgnoreCaseBotName( String str){
        return this.botName.compareToIgnoreCase(str);
    }

     public  boolean matchesRedditAccount( String regex){
        return this.redditAccount.matches(regex);
    }

     public  String[] splitWikiUrl( String regex){
        return this.wikiUrl.split(regex);
    }

     public  void waitJsonDirectory()throws InterruptedException{
        this.jsonDirectory.wait();
    }

     public  int compareToDiscordToken( String anotherString){
        return this.discordToken.compareTo(anotherString);
    }

     public  void waitInviteSupportServer( long timeoutMillis,  int nanos)throws InterruptedException{
        this.inviteSupportServer.wait(timeoutMillis,nanos);
    }

     public  String stripWikiUrl(){
        return this.wikiUrl.strip();
    }

     public  String internGlobalPrefix(){
        return this.globalPrefix.intern();
    }

     public  String stripLeadingRedditSecret(){
        return this.redditSecret.stripLeading();
    }

     public  char[] toCharArrayWikiUrl(){
        return this.wikiUrl.toCharArray();
    }

     public  String repeatWikiUrl( int count){
        return this.wikiUrl.repeat(count);
    }

     public  void notifyAllRedditSecret(){
        this.redditSecret.notifyAll();
    }

     public  IntStream codePointsBotName(){
        return this.botName.codePoints();
    }

     public  void notifyAllBotName(){
        this.botName.notifyAll();
    }

     public  void notifyAllRedditAccount(){
        this.redditAccount.notifyAll();
    }

     public  String replaceRedditId( CharSequence target,  CharSequence replacement){
        return this.redditId.replace(target,replacement);
    }

     public  void getBytesInviteSupportServer( int srcBegin,  int srcEnd,  byte[] dst,  int dstBegin){
        this.inviteSupportServer.getBytes(srcBegin,srcEnd,dst,dstBegin);
    }

     public  String getDiscordToken(){
        return this.discordToken;
    }

     public  String replaceAllWikiUrl( String regex,  String replacement){
        return this.wikiUrl.replaceAll(regex,replacement);
    }

     public  boolean startsWithInviteSupportServer( String prefix){
        return this.inviteSupportServer.startsWith(prefix);
    }

     public  int codePointBeforeRedditAccount( int index){
        return this.redditAccount.codePointBefore(index);
    }

     public  Path getParentJsonDirectory(){
        return this.jsonDirectory.getParent();
    }

     public  int indexOfInviteSupportServer( String str,  int fromIndex){
        return this.inviteSupportServer.indexOf(str,fromIndex);
    }

     public  int lastIndexOfBotName( String str){
        return this.botName.lastIndexOf(str);
    }

     public  String trimDiscordToken(){
        return this.discordToken.trim();
    }

     public  String replaceFirstWikiUrl( String regex,  String replacement){
        return this.wikiUrl.replaceFirst(regex,replacement);
    }

     public  IntStream charsRedditSecret(){
        return this.redditSecret.chars();
    }

     public  String toUpperCaseRedditSecret(){
        return this.redditSecret.toUpperCase();
    }

     public  boolean regionMatchesRedditAccount( int toffset,  String other,  int ooffset,  int len){
        return this.redditAccount.regionMatches(toffset,other,ooffset,len);
    }

     public  char[] toCharArrayDiscordToken(){
        return this.discordToken.toCharArray();
    }

     public  int codePointAtRedditSecret( int index){
        return this.redditSecret.codePointAt(index);
    }

     public  char charAtDiscordToken( int index){
        return this.discordToken.charAt(index);
    }

     public  String toUpperCaseBotName(){
        return this.botName.toUpperCase();
    }

     public  int indexOfDiscordToken( String str,  int fromIndex){
        return this.discordToken.indexOf(str,fromIndex);
    }

     public  boolean isBlankDiscordToken(){
        return this.discordToken.isBlank();
    }

     public  int compareToIgnoreCaseDiscordToken( String str){
        return this.discordToken.compareToIgnoreCase(str);
    }

     public  boolean endsWithRedditId( String suffix){
        return this.redditId.endsWith(suffix);
    }

     public  String internDiscordToken(){
        return this.discordToken.intern();
    }

     public  boolean matchesGlobalPrefix( String regex){
        return this.globalPrefix.matches(regex);
    }

     public  String concatWikiUrl( String str){
        return this.wikiUrl.concat(str);
    }

     public  byte[] getBytesWikiUrl(){
        return this.wikiUrl.getBytes();
    }

     public  boolean containsWikiUrl( CharSequence s){
        return this.wikiUrl.contains(s);
    }

     public  CharSequence subSequenceBotName( int beginIndex,  int endIndex){
        return this.botName.subSequence(beginIndex,endIndex);
    }

     public  void waitRedditSecret()throws InterruptedException{
        this.redditSecret.wait();
    }

     public  int codePointBeforeInviteSupportServer( int index){
        return this.inviteSupportServer.codePointBefore(index);
    }

     public  boolean contentEqualsDiscordToken( StringBuffer sb){
        return this.discordToken.contentEquals(sb);
    }

     public  boolean isEmptyRedditSecret(){
        return this.redditSecret.isEmpty();
    }

     public  int offsetByCodePointsGlobalPrefix( int index,  int codePointOffset){
        return this.globalPrefix.offsetByCodePoints(index,codePointOffset);
    }

     public  void setGlobalPrefix( String globalPrefix){
        this.globalPrefix = globalPrefix;
    }

     public  boolean regionMatchesInviteSupportServer( boolean ignoreCase,  int toffset,  String other,  int ooffset,  int len){
        return this.inviteSupportServer.regionMatches(ignoreCase,toffset,other,ooffset,len);
    }

     public  int codePointCountDiscordToken( int beginIndex,  int endIndex){
        return this.discordToken.codePointCount(beginIndex,endIndex);
    }

     public  boolean equalsIgnoreCaseDiscordToken( String anotherString){
        return this.discordToken.equalsIgnoreCase(anotherString);
    }

     public  String concatGlobalPrefix( String str){
        return this.globalPrefix.concat(str);
    }

     public  String stripLeadingWikiUrl(){
        return this.wikiUrl.stripLeading();
    }

     public  int hashCodeGlobalPrefix(){
        return this.globalPrefix.hashCode();
    }

     public  int compareToIgnoreCaseWikiUrl( String str){
        return this.wikiUrl.compareToIgnoreCase(str);
    }

     public  String stripLeadingGlobalPrefix(){
        return this.globalPrefix.stripLeading();
    }

     public  String substringWikiUrl( int beginIndex){
        return this.wikiUrl.substring(beginIndex);
    }

     public  String toUpperCaseWikiUrl(){
        return this.wikiUrl.toUpperCase();
    }

     public  String stripInviteSupportServer(){
        return this.inviteSupportServer.strip();
    }

     public  CharSequence subSequenceDiscordToken( int beginIndex,  int endIndex){
        return this.discordToken.subSequence(beginIndex,endIndex);
    }

     public  char[] toCharArrayGlobalPrefix(){
        return this.globalPrefix.toCharArray();
    }

     public  int hashCodeJsonDirectory(){
        return this.jsonDirectory.hashCode();
    }

     public  int codePointCountWikiUrl( int beginIndex,  int endIndex){
        return this.wikiUrl.codePointCount(beginIndex,endIndex);
    }

     public  boolean startsWithDiscordToken( String prefix,  int toffset){
        return this.discordToken.startsWith(prefix,toffset);
    }

     public  boolean regionMatchesDiscordToken( int toffset,  String other,  int ooffset,  int len){
        return this.discordToken.regionMatches(toffset,other,ooffset,len);
    }

     public  String replaceFirstBotName( String regex,  String replacement){
        return this.botName.replaceFirst(regex,replacement);
    }

     public  String toStringJsonDirectory(){
        return this.jsonDirectory.toString();
    }

     public  char[] toCharArrayRedditAccount(){
        return this.redditAccount.toCharArray();
    }

     public  void getBytesBotName( int srcBegin,  int srcEnd,  byte[] dst,  int dstBegin){
        this.botName.getBytes(srcBegin,srcEnd,dst,dstBegin);
    }

     public  int lastIndexOfRedditAccount( int ch,  int fromIndex){
        return this.redditAccount.lastIndexOf(ch,fromIndex);
    }

     public  String[] splitInviteSupportServer( String regex,  int limit){
        return this.inviteSupportServer.split(regex,limit);
    }

     public  boolean startsWithGlobalPrefix( String prefix){
        return this.globalPrefix.startsWith(prefix);
    }

     public  void setRedditId( String redditId){
        this.redditId = redditId;
    }

     public  String toLowerCaseBotName(){
        return this.botName.toLowerCase();
    }

     public  int lastIndexOfGlobalPrefix( String str,  int fromIndex){
        return this.globalPrefix.lastIndexOf(str,fromIndex);
    }

     public  String substringDiscordToken( int beginIndex,  int endIndex){
        return this.discordToken.substring(beginIndex,endIndex);
    }

     public  boolean regionMatchesBotName( int toffset,  String other,  int ooffset,  int len){
        return this.botName.regionMatches(toffset,other,ooffset,len);
    }

     public  int getStatusMessageUpdateInterval(){
        return this.statusMessageUpdateInterval;
    }

     public  String trimBotName(){
        return this.botName.trim();
    }

     public  String stripLeadingRedditAccount(){
        return this.redditAccount.stripLeading();
    }

     public  String substringRedditId( int beginIndex,  int endIndex){
        return this.redditId.substring(beginIndex,endIndex);
    }

     public  IntStream charsRedditAccount(){
        return this.redditAccount.chars();
    }

     public  Stream<String> linesRedditId(){
        return this.redditId.lines();
    }

     public  IntStream charsGlobalPrefix(){
        return this.globalPrefix.chars();
    }

     public  int offsetByCodePointsInviteSupportServer( int index,  int codePointOffset){
        return this.inviteSupportServer.offsetByCodePoints(index,codePointOffset);
    }

     public  boolean endsWithRedditAccount( String suffix){
        return this.redditAccount.endsWith(suffix);
    }

     public  void setWikiUrl( String wikiUrl){
        this.wikiUrl = wikiUrl;
    }

     public  char charAtInviteSupportServer( int index){
        return this.inviteSupportServer.charAt(index);
    }

     public  String internRedditSecret(){
        return this.redditSecret.intern();
    }

     public  int lastIndexOfWikiUrl( String str,  int fromIndex){
        return this.wikiUrl.lastIndexOf(str,fromIndex);
    }

     public  String[] splitInviteSupportServer( String regex){
        return this.inviteSupportServer.split(regex);
    }

     public  int hashCodeRedditId(){
        return this.redditId.hashCode();
    }

     public  boolean regionMatchesGlobalPrefix( int toffset,  String other,  int ooffset,  int len){
        return this.globalPrefix.regionMatches(toffset,other,ooffset,len);
    }

     public  int compareToInviteSupportServer( String anotherString){
        return this.inviteSupportServer.compareTo(anotherString);
    }

     public  String toUpperCaseInviteSupportServer( Locale locale){
        return this.inviteSupportServer.toUpperCase(locale);
    }

     public  String stripGlobalPrefix(){
        return this.globalPrefix.strip();
    }

     public  void waitDiscordToken( long timeoutMillis)throws InterruptedException{
        this.discordToken.wait(timeoutMillis);
    }

     public  int indexOfRedditSecret( int ch){
        return this.redditSecret.indexOf(ch);
    }

     public  int indexOfRedditSecret( int ch,  int fromIndex){
        return this.redditSecret.indexOf(ch,fromIndex);
    }

     public  void waitJsonDirectory( long timeoutMillis)throws InterruptedException{
        this.jsonDirectory.wait(timeoutMillis);
    }

     public  boolean startsWithBotName( String prefix,  int toffset){
        return this.botName.startsWith(prefix,toffset);
    }

     public  String trimRedditAccount(){
        return this.redditAccount.trim();
    }

     public  IntStream codePointsInviteSupportServer(){
        return this.inviteSupportServer.codePoints();
    }

     public  IntStream codePointsRedditSecret(){
        return this.redditSecret.codePoints();
    }

     public  void notifyAllInviteSupportServer(){
        this.inviteSupportServer.notifyAll();
    }

     public  String replaceGlobalPrefix( char oldChar,  char newChar){
        return this.globalPrefix.replace(oldChar,newChar);
    }

     public  byte[] getBytesInviteSupportServer(){
        return this.inviteSupportServer.getBytes();
    }

     public  int compareToIgnoreCaseInviteSupportServer( String str){
        return this.inviteSupportServer.compareToIgnoreCase(str);
    }

     public  int getInteractiveMessageLifetime(){
        return this.interactiveMessageLifetime;
    }

     public  int codePointAtRedditId( int index){
        return this.redditId.codePointAt(index);
    }

     public  boolean contentEqualsRedditId( StringBuffer sb){
        return this.redditId.contentEquals(sb);
    }

     public  byte[] getBytesRedditAccount(){
        return this.redditAccount.getBytes();
    }

     public  boolean contentEqualsRedditId( CharSequence cs){
        return this.redditId.contentEquals(cs);
    }

     public  boolean isEmptyWikiUrl(){
        return this.wikiUrl.isEmpty();
    }

     public  int lastIndexOfWikiUrl( String str){
        return this.wikiUrl.lastIndexOf(str);
    }

     public  String toLowerCaseBotName( Locale locale){
        return this.botName.toLowerCase(locale);
    }

     public  boolean isEmptyRedditAccount(){
        return this.redditAccount.isEmpty();
    }

     public  int indexOfBotName( int ch){
        return this.botName.indexOf(ch);
    }

     public  int offsetByCodePointsRedditId( int index,  int codePointOffset){
        return this.redditId.offsetByCodePoints(index,codePointOffset);
    }

     public  String stripTrailingWikiUrl(){
        return this.wikiUrl.stripTrailing();
    }

     public  String concatDiscordToken( String str){
        return this.discordToken.concat(str);
    }

     public  void waitDiscordToken()throws InterruptedException{
        this.discordToken.wait();
    }

     public  String stripDiscordToken(){
        return this.discordToken.strip();
    }

     public  int codePointAtGlobalPrefix( int index){
        return this.globalPrefix.codePointAt(index);
    }

     public  boolean regionMatchesBotName( boolean ignoreCase,  int toffset,  String other,  int ooffset,  int len){
        return this.botName.regionMatches(ignoreCase,toffset,other,ooffset,len);
    }

     public  void setDiscordToken( String discordToken){
        this.discordToken = discordToken;
    }

     public  String substringRedditSecret( int beginIndex,  int endIndex){
        return this.redditSecret.substring(beginIndex,endIndex);
    }

     public  String toUpperCaseBotName( Locale locale){
        return this.botName.toUpperCase(locale);
    }

     public  String substringInviteSupportServer( int beginIndex){
        return this.inviteSupportServer.substring(beginIndex);
    }

     public  int lastIndexOfInviteSupportServer( String str){
        return this.inviteSupportServer.lastIndexOf(str);
    }

     public  Path resolveJsonDirectory( String other){
        return this.jsonDirectory.resolve(other);
    }

     public  String repeatInviteSupportServer( int count){
        return this.inviteSupportServer.repeat(count);
    }

     public  String stripLeadingBotName(){
        return this.botName.stripLeading();
    }

     public  IntStream charsBotName(){
        return this.botName.chars();
    }

     public  boolean contentEqualsDiscordToken( CharSequence cs){
        return this.discordToken.contentEquals(cs);
    }

     public  int codePointAtInviteSupportServer( int index){
        return this.inviteSupportServer.codePointAt(index);
    }

     public  char[] toCharArrayRedditSecret(){
        return this.redditSecret.toCharArray();
    }

     public  String replaceFirstRedditSecret( String regex,  String replacement){
        return this.redditSecret.replaceFirst(regex,replacement);
    }

     public  boolean contentEqualsGlobalPrefix( StringBuffer sb){
        return this.globalPrefix.contentEquals(sb);
    }

     public  boolean startsWithJsonDirectory( String other){
        return this.jsonDirectory.startsWith(other);
    }

     public  String repeatGlobalPrefix( int count){
        return this.globalPrefix.repeat(count);
    }

     public  String[] splitRedditAccount( String regex,  int limit){
        return this.redditAccount.split(regex,limit);
    }

     public  int codePointCountInviteSupportServer( int beginIndex,  int endIndex){
        return this.inviteSupportServer.codePointCount(beginIndex,endIndex);
    }

     public  int compareToRedditId( String anotherString){
        return this.redditId.compareTo(anotherString);
    }

     public  Iterator<Path> iteratorJsonDirectory(){
        return this.jsonDirectory.iterator();
    }

     public  int indexOfDiscordToken( int ch){
        return this.discordToken.indexOf(ch);
    }

     public  String replaceWikiUrl( CharSequence target,  CharSequence replacement){
        return this.wikiUrl.replace(target,replacement);
    }

     public  int compareToIgnoreCaseRedditSecret( String str){
        return this.redditSecret.compareToIgnoreCase(str);
    }

     public  boolean containsInviteSupportServer( CharSequence s){
        return this.inviteSupportServer.contains(s);
    }

     public  int lastIndexOfInviteSupportServer( int ch){
        return this.inviteSupportServer.lastIndexOf(ch);
    }

     public  char charAtBotName( int index){
        return this.botName.charAt(index);
    }

     public  void getBytesRedditSecret( int srcBegin,  int srcEnd,  byte[] dst,  int dstBegin){
        this.redditSecret.getBytes(srcBegin,srcEnd,dst,dstBegin);
    }

     public  String repeatBotName( int count){
        return this.botName.repeat(count);
    }

     public  int codePointAtDiscordToken( int index){
        return this.discordToken.codePointAt(index);
    }

     public  void notifyInviteSupportServer(){
        this.inviteSupportServer.notify();
    }

     public  int lastIndexOfRedditId( int ch){
        return this.redditId.lastIndexOf(ch);
    }

     public  void setInteractiveMessageLifetime( int interactiveMessageLifetime){
        this.interactiveMessageLifetime = interactiveMessageLifetime;
    }

     public  String toLowerCaseRedditSecret( Locale locale){
        return this.redditSecret.toLowerCase(locale);
    }

     public  String getRedditAccount(){
        return this.redditAccount;
    }

     public  String trimRedditId(){
        return this.redditId.trim();
    }

     public  String concatRedditId( String str){
        return this.redditId.concat(str);
    }

     public  boolean isBlankRedditAccount(){
        return this.redditAccount.isBlank();
    }

     public  String trimWikiUrl(){
        return this.wikiUrl.trim();
    }

     public  char[] toCharArrayBotName(){
        return this.botName.toCharArray();
    }

     public  String replaceRedditAccount( CharSequence target,  CharSequence replacement){
        return this.redditAccount.replace(target,replacement);
    }

     public  int compareToIgnoreCaseGlobalPrefix( String str){
        return this.globalPrefix.compareToIgnoreCase(str);
    }

     public  int lastIndexOfRedditAccount( String str){
        return this.redditAccount.lastIndexOf(str);
    }

     public  void notifyAllWikiUrl(){
        this.wikiUrl.notifyAll();
    }

     public  boolean containsRedditSecret( CharSequence s){
        return this.redditSecret.contains(s);
    }

     public  String[] splitRedditSecret( String regex,  int limit){
        return this.redditSecret.split(regex,limit);
    }

     public  Path resolveJsonDirectory( Path other){
        return this.jsonDirectory.resolve(other);
    }

     public  int getNameCountJsonDirectory(){
        return this.jsonDirectory.getNameCount();
    }

     public  CharSequence subSequenceInviteSupportServer( int beginIndex,  int endIndex){
        return this.inviteSupportServer.subSequence(beginIndex,endIndex);
    }

     public  String[] splitRedditAccount( String regex){
        return this.redditAccount.split(regex);
    }

     public  int codePointBeforeBotName( int index){
        return this.botName.codePointBefore(index);
    }

     public  String stripRedditId(){
        return this.redditId.strip();
    }

     public  Path relativizeJsonDirectory( Path other){
        return this.jsonDirectory.relativize(other);
    }

     public  int indexOfGlobalPrefix( int ch,  int fromIndex){
        return this.globalPrefix.indexOf(ch,fromIndex);
    }

     public  int codePointBeforeRedditSecret( int index){
        return this.redditSecret.codePointBefore(index);
    }

     public  boolean equalsIgnoreCaseWikiUrl( String anotherString){
        return this.wikiUrl.equalsIgnoreCase(anotherString);
    }

     public  int offsetByCodePointsWikiUrl( int index,  int codePointOffset){
        return this.wikiUrl.offsetByCodePoints(index,codePointOffset);
    }

     public  Path getNameJsonDirectory( int index){
        return this.jsonDirectory.getName(index);
    }

     public  String stripLeadingRedditId(){
        return this.redditId.stripLeading();
    }

     public  boolean endsWithRedditSecret( String suffix){
        return this.redditSecret.endsWith(suffix);
    }

     public  String[] splitBotName( String regex){
        return this.botName.split(regex);
    }

     public  void getBytesWikiUrl( int srcBegin,  int srcEnd,  byte[] dst,  int dstBegin){
        this.wikiUrl.getBytes(srcBegin,srcEnd,dst,dstBegin);
    }

     public  void waitWikiUrl( long timeoutMillis,  int nanos)throws InterruptedException{
        this.wikiUrl.wait(timeoutMillis,nanos);
    }

     public  void waitRedditId( long timeoutMillis,  int nanos)throws InterruptedException{
        this.redditId.wait(timeoutMillis,nanos);
    }

     public  void notifyBotName(){
        this.botName.notify();
    }

     public  void getBytesRedditId( int srcBegin,  int srcEnd,  byte[] dst,  int dstBegin){
        this.redditId.getBytes(srcBegin,srcEnd,dst,dstBegin);
    }

     public  String stripRedditAccount(){
        return this.redditAccount.strip();
    }

     public  String toStringGlobalPrefix(){
        return this.globalPrefix.toString();
    }

     public  void getBytesDiscordToken( int srcBegin,  int srcEnd,  byte[] dst,  int dstBegin){
        this.discordToken.getBytes(srcBegin,srcEnd,dst,dstBegin);
    }

     public  boolean regionMatchesRedditId( int toffset,  String other,  int ooffset,  int len){
        return this.redditId.regionMatches(toffset,other,ooffset,len);
    }

     public  boolean equalsWikiUrl( Object anObject){
        return this.wikiUrl.equals(anObject);
    }

     public  String toStringRedditId(){
        return this.redditId.toString();
    }

     public  Stream<String> linesRedditAccount(){
        return this.redditAccount.lines();
    }

     public  String repeatRedditAccount( int count){
        return this.redditAccount.repeat(count);
    }

     public  void waitGlobalPrefix( long timeoutMillis,  int nanos)throws InterruptedException{
        this.globalPrefix.wait(timeoutMillis,nanos);
    }

     public  boolean isEmptyBotName(){
        return this.botName.isEmpty();
    }

     public  String substringBotName( int beginIndex){
        return this.botName.substring(beginIndex);
    }

     public  boolean startsWithRedditAccount( String prefix,  int toffset){
        return this.redditAccount.startsWith(prefix,toffset);
    }

     public  void setImageHeight( int imageHeight){
        this.imageHeight = imageHeight;
    }

     public  boolean contentEqualsWikiUrl( StringBuffer sb){
        return this.wikiUrl.contentEquals(sb);
    }

     public  boolean startsWithRedditSecret( String prefix,  int toffset){
        return this.redditSecret.startsWith(prefix,toffset);
    }

     public  void waitRedditId()throws InterruptedException{
        this.redditId.wait();
    }

     public  IntStream codePointsRedditAccount(){
        return this.redditAccount.codePoints();
    }

     public  boolean matchesDiscordToken( String regex){
        return this.discordToken.matches(regex);
    }

     public  boolean startsWithWikiUrl( String prefix){
        return this.wikiUrl.startsWith(prefix);
    }

     public  void waitGlobalPrefix( long timeoutMillis)throws InterruptedException{
        this.globalPrefix.wait(timeoutMillis);
    }

     public  String replaceRedditId( char oldChar,  char newChar){
        return this.redditId.replace(oldChar,newChar);
    }

     public  int lastIndexOfRedditAccount( String str,  int fromIndex){
        return this.redditAccount.lastIndexOf(str,fromIndex);
    }

     public  int indexOfWikiUrl( String str,  int fromIndex){
        return this.wikiUrl.indexOf(str,fromIndex);
    }

     public  boolean equalsIgnoreCaseRedditAccount( String anotherString){
        return this.redditAccount.equalsIgnoreCase(anotherString);
    }

     public  int lastIndexOfGlobalPrefix( int ch,  int fromIndex){
        return this.globalPrefix.lastIndexOf(ch,fromIndex);
    }

     public  int lengthBotName(){
        return this.botName.length();
    }

     public  int hashCodeBotName(){
        return this.botName.hashCode();
    }

     public  String[] splitDiscordToken( String regex){
        return this.discordToken.split(regex);
    }

     public  int indexOfGlobalPrefix( int ch){
        return this.globalPrefix.indexOf(ch);
    }

     public  boolean equalsRedditId( Object anObject){
        return this.redditId.equals(anObject);
    }

     public  String replaceAllRedditAccount( String regex,  String replacement){
        return this.redditAccount.replaceAll(regex,replacement);
    }

     public  String toLowerCaseWikiUrl( Locale locale){
        return this.wikiUrl.toLowerCase(locale);
    }

     public  IntStream codePointsDiscordToken(){
        return this.discordToken.codePoints();
    }

     public  String toLowerCaseRedditAccount( Locale locale){
        return this.redditAccount.toLowerCase(locale);
    }

     public  byte[] getBytesGlobalPrefix(){
        return this.globalPrefix.getBytes();
    }

     public  int codePointCountRedditSecret( int beginIndex,  int endIndex){
        return this.redditSecret.codePointCount(beginIndex,endIndex);
    }

     public  String getGlobalPrefix(){
        return this.globalPrefix;
    }

     public  void notifyRedditSecret(){
        this.redditSecret.notify();
    }

     public  boolean contentEqualsRedditSecret( StringBuffer sb){
        return this.redditSecret.contentEquals(sb);
    }

     public  int indexOfRedditId( int ch){
        return this.redditId.indexOf(ch);
    }

     public  String[] splitRedditSecret( String regex){
        return this.redditSecret.split(regex);
    }

     public  int lastIndexOfRedditAccount( int ch){
        return this.redditAccount.lastIndexOf(ch);
    }

     public  int indexOfRedditSecret( String str,  int fromIndex){
        return this.redditSecret.indexOf(str,fromIndex);
    }

     public  int hashCodeDiscordToken(){
        return this.discordToken.hashCode();
    }

     public  String internRedditAccount(){
        return this.redditAccount.intern();
    }

     public  char charAtWikiUrl( int index){
        return this.wikiUrl.charAt(index);
    }

     public  boolean matchesBotName( String regex){
        return this.botName.matches(regex);
    }

     public  void notifyWikiUrl(){
        this.wikiUrl.notify();
    }

     public  String replaceDiscordToken( CharSequence target,  CharSequence replacement){
        return this.discordToken.replace(target,replacement);
    }

     public  void waitBotName( long timeoutMillis,  int nanos)throws InterruptedException{
        this.botName.wait(timeoutMillis,nanos);
    }

     public  String internInviteSupportServer(){
        return this.inviteSupportServer.intern();
    }

     public  int indexOfRedditId( String str,  int fromIndex){
        return this.redditId.indexOf(str,fromIndex);
    }

     public  boolean contentEqualsRedditSecret( CharSequence cs){
        return this.redditSecret.contentEquals(cs);
    }

     public  String repeatRedditSecret( int count){
        return this.redditSecret.repeat(count);
    }

     public  String substringInviteSupportServer( int beginIndex,  int endIndex){
        return this.inviteSupportServer.substring(beginIndex,endIndex);
    }

     public  boolean containsBotName( CharSequence s){
        return this.botName.contains(s);
    }

     public  char[] toCharArrayRedditId(){
        return this.redditId.toCharArray();
    }

     public  void notifyAllJsonDirectory(){
        this.jsonDirectory.notifyAll();
    }

     public  boolean startsWithJsonDirectory( Path other){
        return this.jsonDirectory.startsWith(other);
    }

     public  String stripTrailingGlobalPrefix(){
        return this.globalPrefix.stripTrailing();
    }

     public  IntStream charsDiscordToken(){
        return this.discordToken.chars();
    }

     public  boolean isBlankWikiUrl(){
        return this.wikiUrl.isBlank();
    }

     public  String stripRedditSecret(){
        return this.redditSecret.strip();
    }

     public  boolean endsWithDiscordToken( String suffix){
        return this.discordToken.endsWith(suffix);
    }

     public  boolean contentEqualsBotName( StringBuffer sb){
        return this.botName.contentEquals(sb);
    }

     public  boolean containsDiscordToken( CharSequence s){
        return this.discordToken.contains(s);
    }

     public  String replaceRedditSecret( char oldChar,  char newChar){
        return this.redditSecret.replace(oldChar,newChar);
    }

     public  char charAtGlobalPrefix( int index){
        return this.globalPrefix.charAt(index);
    }

     public  boolean contentEqualsBotName( CharSequence cs){
        return this.botName.contentEquals(cs);
    }

     public  boolean startsWithInviteSupportServer( String prefix,  int toffset){
        return this.inviteSupportServer.startsWith(prefix,toffset);
    }

     public  int compareToIgnoreCaseRedditId( String str){
        return this.redditId.compareToIgnoreCase(str);
    }

     public  boolean endsWithBotName( String suffix){
        return this.botName.endsWith(suffix);
    }

     public  int indexOfRedditId( int ch,  int fromIndex){
        return this.redditId.indexOf(ch,fromIndex);
    }

     public  String replaceFirstRedditAccount( String regex,  String replacement){
        return this.redditAccount.replaceFirst(regex,replacement);
    }

     public  void setBotName( String botName){
        this.botName = botName;
    }

     public  String toLowerCaseRedditId(){
        return this.redditId.toLowerCase();
    }

     public  boolean endsWithGlobalPrefix( String suffix){
        return this.globalPrefix.endsWith(suffix);
    }

     public  void setImageWidth( int imageWidth){
        this.imageWidth = imageWidth;
    }

     public  boolean startsWithRedditAccount( String prefix){
        return this.redditAccount.startsWith(prefix);
    }

     public  IntStream codePointsGlobalPrefix(){
        return this.globalPrefix.codePoints();
    }

     public  String toUpperCaseGlobalPrefix( Locale locale){
        return this.globalPrefix.toUpperCase(locale);
    }

     public  boolean regionMatchesRedditSecret( int toffset,  String other,  int ooffset,  int len){
        return this.redditSecret.regionMatches(toffset,other,ooffset,len);
    }

     public  String stripTrailingRedditId(){
        return this.redditId.stripTrailing();
    }

     public  int offsetByCodePointsBotName( int index,  int codePointOffset){
        return this.botName.offsetByCodePoints(index,codePointOffset);
    }

     public  int lastIndexOfGlobalPrefix( int ch){
        return this.globalPrefix.lastIndexOf(ch);
    }

     public  void waitInviteSupportServer( long timeoutMillis)throws InterruptedException{
        this.inviteSupportServer.wait(timeoutMillis);
    }

     public  String toStringDiscordToken(){
        return this.discordToken.toString();
    }

     public  void waitJsonDirectory( long timeoutMillis,  int nanos)throws InterruptedException{
        this.jsonDirectory.wait(timeoutMillis,nanos);
    }

     public  void setRedditAccount( String redditAccount){
        this.redditAccount = redditAccount;
    }

     public  String stripTrailingRedditSecret(){
        return this.redditSecret.stripTrailing();
    }

     public  void waitInviteSupportServer()throws InterruptedException{
        this.inviteSupportServer.wait();
    }

     public  boolean containsRedditAccount( CharSequence s){
        return this.redditAccount.contains(s);
    }

     public  String replaceWikiUrl( char oldChar,  char newChar){
        return this.wikiUrl.replace(oldChar,newChar);
    }

     public  boolean contentEqualsRedditAccount( StringBuffer sb){
        return this.redditAccount.contentEquals(sb);
    }

     public  String toUpperCaseRedditAccount(){
        return this.redditAccount.toUpperCase();
    }

     public  void waitGlobalPrefix()throws InterruptedException{
        this.globalPrefix.wait();
    }

     public  String stripTrailingRedditAccount(){
        return this.redditAccount.stripTrailing();
    }

     public  boolean regionMatchesDiscordToken( boolean ignoreCase,  int toffset,  String other,  int ooffset,  int len){
        return this.discordToken.regionMatches(ignoreCase,toffset,other,ooffset,len);
    }

     public  String replaceFirstGlobalPrefix( String regex,  String replacement){
        return this.globalPrefix.replaceFirst(regex,replacement);
    }

     public  void waitRedditSecret( long timeoutMillis,  int nanos)throws InterruptedException{
        this.redditSecret.wait(timeoutMillis,nanos);
    }

     public  boolean regionMatchesRedditAccount( boolean ignoreCase,  int toffset,  String other,  int ooffset,  int len){
        return this.redditAccount.regionMatches(ignoreCase,toffset,other,ooffset,len);
    }

     public  int lastIndexOfInviteSupportServer( int ch,  int fromIndex){
        return this.inviteSupportServer.lastIndexOf(ch,fromIndex);
    }

     public  boolean endsWithWikiUrl( String suffix){
        return this.wikiUrl.endsWith(suffix);
    }

     public  String toLowerCaseGlobalPrefix( Locale locale){
        return this.globalPrefix.toLowerCase(locale);
    }

}