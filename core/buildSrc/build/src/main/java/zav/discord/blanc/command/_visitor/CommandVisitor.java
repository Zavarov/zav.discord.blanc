
package zav.discord.blanc.command._visitor;

import zav.discord.blanc.command.*;



 public  interface CommandVisitor {
    default  public  CommandVisitor getRealThis(){
        return this;
    }
    default void handle( Command command){

        this.walkUpFrom(command);
        this.traverse(command);
        this.endWalkUpFrom(command);
    }
    default void visit( Command command){

    }
    default void traverse( Command command){

    



    }
    default void endVisit( Command command){

    }
    default void walkUpFrom( Command command){

        this.visit(command);

    }
    default void endWalkUpFrom( Command command){

        this.endVisit(command);
    }
    default void handle( MessageCommand messageCommand){

        this.walkUpFrom(messageCommand);
        this.traverse(messageCommand);
        this.endWalkUpFrom(messageCommand);
    }
    default void visit( MessageCommand messageCommand){

    }
    default void traverse( MessageCommand messageCommand){


    }
    default void endVisit( MessageCommand messageCommand){

    }
    default void walkUpFrom( MessageCommand messageCommand){

        this.visit(messageCommand);
        this.walkUpFrom((Command)messageCommand);

    }
    default void endWalkUpFrom( MessageCommand messageCommand){

        this.endWalkUpFrom((Command)messageCommand);
        this.endVisit(messageCommand);
    }
    default void handle( GuildCommand guildCommand){

        this.walkUpFrom(guildCommand);
        this.traverse(guildCommand);
        this.endWalkUpFrom(guildCommand);
    }
    default void visit( GuildCommand guildCommand){

    }
    default void traverse( GuildCommand guildCommand){


    }
    default void endVisit( GuildCommand guildCommand){

    }
    default void walkUpFrom( GuildCommand guildCommand){

        this.visit(guildCommand);
        this.walkUpFrom((Command)guildCommand);

    }
    default void endWalkUpFrom( GuildCommand guildCommand){

        this.endWalkUpFrom((Command)guildCommand);
        this.endVisit(guildCommand);
    }
    default void handle( CommandBuilder commandBuilder){

        this.walkUpFrom(commandBuilder);
        this.traverse(commandBuilder);
        this.endWalkUpFrom(commandBuilder);
    }
    default void visit( CommandBuilder commandBuilder){

    }
    default void traverse( CommandBuilder commandBuilder){


    }
    default void endVisit( CommandBuilder commandBuilder){

    }
    default void walkUpFrom( CommandBuilder commandBuilder){

        this.visit(commandBuilder);

    }
    default void endWalkUpFrom( CommandBuilder commandBuilder){

        this.endVisit(commandBuilder);
    }
}