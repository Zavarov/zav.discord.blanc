package vartas.discord.bot.visitor;

import com.google.common.base.Preconditions;
import vartas.discord.bot.listener.ActivityListener;

import javax.annotation.Nonnull;

public interface ActivityListenerVisitor {
    default void visit(@Nonnull ActivityListener activityListener){}

    default void traverse(@Nonnull ActivityListener activityListener) {}

    default void endVisit(@Nonnull ActivityListener activityListener){}

    default void handle(@Nonnull ActivityListener activityListener) throws NullPointerException{
        Preconditions.checkNotNull(activityListener);
        visit(activityListener);
        traverse(activityListener);
        endVisit(activityListener);
    }
}
