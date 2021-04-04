
package zav.discord.blanc.activity._visitor;

import zav.discord.blanc.activity.*;



 public  interface ActivityVisitor {
    default  public  ActivityVisitor getRealThis(){
        return this;
    }
    default void handle( Activity activity){

        this.walkUpFrom(activity);
        this.traverse(activity);
        this.endWalkUpFrom(activity);
    }
    default void visit( Activity activity){

    }
    default void traverse( Activity activity){

    
        activity.valuesActivity().forEach(_value -> _value.accept(getRealThis()));

    }
    default void endVisit( Activity activity){

    }
    default void walkUpFrom( Activity activity){

        this.visit(activity);

    }
    default void endWalkUpFrom( Activity activity){

        this.endVisit(activity);
    }
    default void handle( GuildActivity guildActivity){

        this.walkUpFrom(guildActivity);
        this.traverse(guildActivity);
        this.endWalkUpFrom(guildActivity);
    }
    default void visit( GuildActivity guildActivity){

    }
    default void traverse( GuildActivity guildActivity){

    

    }
    default void endVisit( GuildActivity guildActivity){

    }
    default void walkUpFrom( GuildActivity guildActivity){

        this.visit(guildActivity);

    }
    default void endWalkUpFrom( GuildActivity guildActivity){

        this.endVisit(guildActivity);
    }
}