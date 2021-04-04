
package zav.discord.blanc.parser._visitor;

import zav.discord.blanc.parser.*;



 public  interface ParserVisitor {
    default  public  ParserVisitor getRealThis(){
        return this;
    }
    default void handle( AbstractTypeResolver abstractTypeResolver){

        this.walkUpFrom(abstractTypeResolver);
        this.traverse(abstractTypeResolver);
        this.endWalkUpFrom(abstractTypeResolver);
    }
    default void visit( AbstractTypeResolver abstractTypeResolver){

    }
    default void traverse( AbstractTypeResolver abstractTypeResolver){


    }
    default void endVisit( AbstractTypeResolver abstractTypeResolver){

    }
    default void walkUpFrom( AbstractTypeResolver abstractTypeResolver){

        this.visit(abstractTypeResolver);

    }
    default void endWalkUpFrom( AbstractTypeResolver abstractTypeResolver){

        this.endVisit(abstractTypeResolver);
    }
    default void handle( Parser parser){

        this.walkUpFrom(parser);
        this.traverse(parser);
        this.endWalkUpFrom(parser);
    }
    default void visit( Parser parser){

    }
    default void traverse( Parser parser){


    }
    default void endVisit( Parser parser){

    }
    default void walkUpFrom( Parser parser){

        this.visit(parser);

    }
    default void endWalkUpFrom( Parser parser){

        this.endVisit(parser);
    }
    default void handle( IntermediateCommand intermediateCommand){

        this.walkUpFrom(intermediateCommand);
        this.traverse(intermediateCommand);
        this.endWalkUpFrom(intermediateCommand);
    }
    default void visit( IntermediateCommand intermediateCommand){

    }
    default void traverse( IntermediateCommand intermediateCommand){


    }
    default void endVisit( IntermediateCommand intermediateCommand){

    }
    default void walkUpFrom( IntermediateCommand intermediateCommand){

        this.visit(intermediateCommand);

    }
    default void endWalkUpFrom( IntermediateCommand intermediateCommand){

        this.endVisit(intermediateCommand);
    }
    default void handle( Argument argument){

        this.walkUpFrom(argument);
        this.traverse(argument);
        this.endWalkUpFrom(argument);
    }
    default void visit( Argument argument){

    }
    default void traverse( Argument argument){


    }
    default void endVisit( Argument argument){

    }
    default void walkUpFrom( Argument argument){

        this.visit(argument);

    }
    default void endWalkUpFrom( Argument argument){

        this.endVisit(argument);
    }
    default void handle( ArithmeticArgument arithmeticArgument){

        this.walkUpFrom(arithmeticArgument);
        this.traverse(arithmeticArgument);
        this.endWalkUpFrom(arithmeticArgument);
    }
    default void visit( ArithmeticArgument arithmeticArgument){

    }
    default void traverse( ArithmeticArgument arithmeticArgument){


    }
    default void endVisit( ArithmeticArgument arithmeticArgument){

    }
    default void walkUpFrom( ArithmeticArgument arithmeticArgument){

        this.visit(arithmeticArgument);
        this.walkUpFrom((Argument)arithmeticArgument);

    }
    default void endWalkUpFrom( ArithmeticArgument arithmeticArgument){

        this.endWalkUpFrom((Argument)arithmeticArgument);
        this.endVisit(arithmeticArgument);
    }
    default void handle( StringArgument stringArgument){

        this.walkUpFrom(stringArgument);
        this.traverse(stringArgument);
        this.endWalkUpFrom(stringArgument);
    }
    default void visit( StringArgument stringArgument){

    }
    default void traverse( StringArgument stringArgument){


    }
    default void endVisit( StringArgument stringArgument){

    }
    default void walkUpFrom( StringArgument stringArgument){

        this.visit(stringArgument);
        this.walkUpFrom((Argument)stringArgument);

    }
    default void endWalkUpFrom( StringArgument stringArgument){

        this.endWalkUpFrom((Argument)stringArgument);
        this.endVisit(stringArgument);
    }
    default void handle( MentionArgument mentionArgument){

        this.walkUpFrom(mentionArgument);
        this.traverse(mentionArgument);
        this.endWalkUpFrom(mentionArgument);
    }
    default void visit( MentionArgument mentionArgument){

    }
    default void traverse( MentionArgument mentionArgument){


    }
    default void endVisit( MentionArgument mentionArgument){

    }
    default void walkUpFrom( MentionArgument mentionArgument){

        this.visit(mentionArgument);
        this.walkUpFrom((Argument)mentionArgument);

    }
    default void endWalkUpFrom( MentionArgument mentionArgument){

        this.endWalkUpFrom((Argument)mentionArgument);
        this.endVisit(mentionArgument);
    }
}