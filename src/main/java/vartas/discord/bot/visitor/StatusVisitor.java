package vartas.discord.bot.visitor;

import com.google.common.base.Preconditions;
import vartas.discord.bot.entities.Status;

import javax.annotation.Nonnull;

public interface StatusVisitor {
    default void visit(@Nonnull Status status){}

    default void traverse(@Nonnull Status status) {}

    default void endVisit(@Nonnull Status status){}

    default void handle(@Nonnull Status status) throws NullPointerException{
        Preconditions.checkNotNull(status);
        visit(status);
        traverse(status);
        endVisit(status);
    }
}
