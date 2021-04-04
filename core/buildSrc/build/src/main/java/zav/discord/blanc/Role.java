
package zav.discord.blanc;

import com.google.common.cache.Cache;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Class;
import java.lang.InterruptedException;
import java.lang.Object;
import java.lang.Runnable;
import java.lang.RuntimeException;
import java.lang.String;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import zav.discord.blanc._visitor.ArchitectureVisitor;
import zav.discord.blanc.activity.Activity;
import zav.jra.models.AbstractLink;
import zav.jra.models.AbstractSubreddit;



 abstract  public  class Role  extends Snowflake  implements Printable {
      private  Optional<String> group = java.util.Optional.empty();
     abstract  public  String getAsMention();

     public  void accept( ArchitectureVisitor visitor){

        visitor.handle(getRealThis());
    }

     public  Role getRealThis(){
        return this;
    }

     public  String toStringGroup(){
        return this.group.toString();
    }

     public  Optional<String> getGroup(){
        return this.group;
    }

     public  Optional<String> orGroup( Supplier<? extends Optional<? extends String>> supplier){
        return this.group.or(supplier);
    }

     public  void waitGroup()throws InterruptedException{
        this.group.wait();
    }

     public  Stream<String> streamGroup(){
        return this.group.stream();
    }

     public  void ifPresentGroup( Consumer<? super String> action){
        this.group.ifPresent(action);
    }

     public  Optional<String> filterGroup( Predicate<? super String> predicate){
        return this.group.filter(predicate);
    }

     public  void setGroup( Optional<String> group){
        this.group = group;
    }

     public  void waitGroup( long timeoutMillis,  int nanos)throws InterruptedException{
        this.group.wait(timeoutMillis,nanos);
    }

     public  void waitGroup( long timeoutMillis)throws InterruptedException{
        this.group.wait(timeoutMillis);
    }

     public  boolean isPresentGroup(){
        return this.group.isPresent();
    }

     public  boolean isEmptyGroup(){
        return this.group.isEmpty();
    }

     public  void notifyAllGroup(){
        this.group.notifyAll();
    }

     public  String orElseThrowGroup(){
        return this.group.orElseThrow();
    }

     public  String orElseGetGroup( Supplier<? extends String> supplier){
        return this.group.orElseGet(supplier);
    }

     public  String orElseGroup( String other){
        return this.group.orElse(other);
    }

     public  void ifPresentOrElseGroup( Consumer<? super String> action,  Runnable emptyAction){
        this.group.ifPresentOrElse(action,emptyAction);
    }

     public  void notifyGroup(){
        this.group.notify();
    }

     public  boolean equalsGroup( Object obj){
        return this.group.equals(obj);
    }

     public  void setGroup( String newValue){

        this.group = Optional.ofNullable(newValue);
    }

     public  int hashCodeGroup(){
        return this.group.hashCode();
    }

}