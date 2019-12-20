package vartas.discord.bot.visitor;

import com.google.common.base.Preconditions;
import vartas.discord.bot.listener.BlacklistListener;

import javax.annotation.Nonnull;

public interface BlacklistListenerVisitor {
    default void visit(@Nonnull BlacklistListener blacklistListener){}

    default void traverse(@Nonnull BlacklistListener blacklistListener) {}

    default void endVisit(@Nonnull BlacklistListener blacklistListener){}

    default void handle(@Nonnull BlacklistListener blacklistListener) throws NullPointerException{
        Preconditions.checkNotNull(blacklistListener);
        visit(blacklistListener);
        traverse(blacklistListener);
        endVisit(blacklistListener);
    }
}
