package vartas.discord.bot.visitor;

import com.google.common.base.Preconditions;
import vartas.discord.bot.entities.Credentials;

import javax.annotation.Nonnull;

public interface CredentialsVisitor {
    default void visit(@Nonnull Credentials credentials){}

    default void traverse(@Nonnull Credentials credentials) {}

    default void endVisit(@Nonnull Credentials credentials){}

    default void handle(@Nonnull Credentials credentials) throws NullPointerException{
        Preconditions.checkNotNull(credentials);
        visit(credentials);
        traverse(credentials);
        endVisit(credentials);
    }
}
