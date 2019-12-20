package vartas.discord.bot.visitor;

import com.google.common.base.Preconditions;
import vartas.discord.bot.EntityAdapter;

import javax.annotation.Nonnull;

public interface EntityAdapterVisitor {
    default void visit(@Nonnull EntityAdapter entityAdapter){}

    default void traverse(@Nonnull EntityAdapter entityAdapter) {}

    default void endVisit(@Nonnull EntityAdapter entityAdapter){}

    default void handle(@Nonnull EntityAdapter entityAdapter) throws NullPointerException{
        Preconditions.checkNotNull(entityAdapter);
        visit(entityAdapter);
        traverse(entityAdapter);
        endVisit(entityAdapter);
    }
}
