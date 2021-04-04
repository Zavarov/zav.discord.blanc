
package zav.discord.blanc.io;

import com.google.common.collect.Multimap;
import java.lang.Class;
import java.lang.InterruptedException;
import java.lang.Iterable;
import java.lang.Object;
import java.lang.String;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import zav.discord.blanc.Rank;
import zav.discord.blanc.io._visitor.IOVisitor;



 public  class StatusMessages   {
      private  List<String> statusMessages = new java.util.concurrent.CopyOnWriteArrayList<>();
     public  void accept( IOVisitor visitor){

        visitor.handle(getRealThis());
    }

     public  StatusMessages getRealThis(){
        return this;
    }

     public  String[] toArrayStatusMessages( String[] a){
        return this.statusMessages.toArray(a);
    }

     public  boolean removeIfStatusMessages( Predicate<? super String> filter){
        return this.statusMessages.removeIf(filter);
    }

     public  void forEachStatusMessages( Consumer<? super String> action){
        this.statusMessages.forEach(action);
    }

     public  boolean containsAllStatusMessages( Collection<?> c){
        return this.statusMessages.containsAll(c);
    }

     public  boolean removeAllStatusMessages( Collection<?> c){
        return this.statusMessages.removeAll(c);
    }

     public  String[] toArrayStatusMessages( IntFunction<String[]> generator){
        return this.statusMessages.toArray(generator);
    }

     public  List<String> getStatusMessages(){
        return this.statusMessages;
    }

     public  void notifyAllStatusMessages(){
        this.statusMessages.notifyAll();
    }

     public  boolean addAllStatusMessages( Collection<? extends String> c){
        return this.statusMessages.addAll(c);
    }

     public  void waitStatusMessages( long timeoutMillis,  int nanos)throws InterruptedException{
        this.statusMessages.wait(timeoutMillis,nanos);
    }

     public  int lastIndexOfStatusMessages( Object o){
        return this.statusMessages.lastIndexOf(o);
    }

     public  int indexOfStatusMessages( Object o){
        return this.statusMessages.indexOf(o);
    }

     public  void addStatusMessages( int index,  String element){
        this.statusMessages.add(index,element);
    }

     public  int sizeStatusMessages(){
        return this.statusMessages.size();
    }

     public  void replaceAllStatusMessages( UnaryOperator<String> operator){
        this.statusMessages.replaceAll(operator);
    }

     public  Stream<String> streamStatusMessages(){
        return this.statusMessages.stream();
    }

     public  ListIterator<String> listIteratorStatusMessages(){
        return this.statusMessages.listIterator();
    }

     public  boolean addStatusMessages( String e){
        return this.statusMessages.add(e);
    }

     public  Iterator<String> iteratorStatusMessages(){
        return this.statusMessages.iterator();
    }

     public  boolean containsStatusMessages( Object o){
        return this.statusMessages.contains(o);
    }

     public  boolean removeStatusMessages( Object o){
        return this.statusMessages.remove(o);
    }

     public  boolean equalsStatusMessages( Object obj){
        return this.statusMessages.equals(obj);
    }

     public  String setStatusMessages( int index,  String element){
        return this.statusMessages.set(index,element);
    }

     public  void waitStatusMessages()throws InterruptedException{
        this.statusMessages.wait();
    }

     public  void notifyStatusMessages(){
        this.statusMessages.notify();
    }

     public  List<String> subListStatusMessages( int fromIndex,  int toIndex){
        return this.statusMessages.subList(fromIndex,toIndex);
    }

     public  String removeStatusMessages( int index){
        return this.statusMessages.remove(index);
    }

     public  Object[] toArrayStatusMessages(){
        return this.statusMessages.toArray();
    }

     public  ListIterator<String> listIteratorStatusMessages( int index){
        return this.statusMessages.listIterator(index);
    }

     public  String toStringStatusMessages(){
        return this.statusMessages.toString();
    }

     public  void waitStatusMessages( long timeoutMillis)throws InterruptedException{
        this.statusMessages.wait(timeoutMillis);
    }

     public  boolean isEmptyStatusMessages(){
        return this.statusMessages.isEmpty();
    }

     public  void clearStatusMessages(){
        this.statusMessages.clear();
    }

     public  Spliterator<String> spliteratorStatusMessages(){
        return this.statusMessages.spliterator();
    }

     public  String getStatusMessages( int index){
        return this.statusMessages.get(index);
    }

     public  void sortStatusMessages( Comparator<? super String> c){
        this.statusMessages.sort(c);
    }

     public  boolean addAllStatusMessages( int index,  Collection<? extends String> c){
        return this.statusMessages.addAll(index,c);
    }

     public  int hashCodeStatusMessages(){
        return this.statusMessages.hashCode();
    }

     public  Stream<String> parallelStreamStatusMessages(){
        return this.statusMessages.parallelStream();
    }

     public  boolean retainAllStatusMessages( Collection<?> c){
        return this.statusMessages.retainAll(c);
    }

     public  void setStatusMessages( List<String> statusMessages){
        this.statusMessages = statusMessages;
    }

}