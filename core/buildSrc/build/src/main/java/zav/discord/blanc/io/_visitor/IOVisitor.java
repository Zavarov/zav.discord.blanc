
package zav.discord.blanc.io._visitor;

import zav.discord.blanc.io.*;



 public  interface IOVisitor {
    default  public  IOVisitor getRealThis(){
        return this;
    }
    default void handle( Credentials credentials){

        this.walkUpFrom(credentials);
        this.traverse(credentials);
        this.endWalkUpFrom(credentials);
    }
    default void visit( Credentials credentials){

    }
    default void traverse( Credentials credentials){


    }
    default void endVisit( Credentials credentials){

    }
    default void walkUpFrom( Credentials credentials){

        this.visit(credentials);

    }
    default void endWalkUpFrom( Credentials credentials){

        this.endVisit(credentials);
    }
    default void handle( Ranks ranks){

        this.walkUpFrom(ranks);
        this.traverse(ranks);
        this.endWalkUpFrom(ranks);
    }
    default void visit( Ranks ranks){

    }
    default void traverse( Ranks ranks){

    

    }
    default void endVisit( Ranks ranks){

    }
    default void walkUpFrom( Ranks ranks){

        this.visit(ranks);

    }
    default void endWalkUpFrom( Ranks ranks){

        this.endVisit(ranks);
    }
    default void handle( StatusMessages statusMessages){

        this.walkUpFrom(statusMessages);
        this.traverse(statusMessages);
        this.endWalkUpFrom(statusMessages);
    }
    default void visit( StatusMessages statusMessages){

    }
    default void traverse( StatusMessages statusMessages){

    



    }
    default void endVisit( StatusMessages statusMessages){

    }
    default void walkUpFrom( StatusMessages statusMessages){

        this.visit(statusMessages);

    }
    default void endWalkUpFrom( StatusMessages statusMessages){

        this.endVisit(statusMessages);
    }
}