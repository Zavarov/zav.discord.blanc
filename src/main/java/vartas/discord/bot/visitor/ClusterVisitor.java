package vartas.discord.bot.visitor;

import com.google.common.base.Preconditions;
import vartas.discord.bot.entities.Cluster;

import javax.annotation.Nonnull;

public interface ClusterVisitor extends RedditFeedVisitor, StatusVisitor, EntityAdapterVisitor, ShardVisitor {
    default void visit(@Nonnull Cluster cluster){}

    default void traverse(@Nonnull Cluster cluster) {
        cluster.accept(this);
    }

    default void endVisit(@Nonnull Cluster cluster){}

    default void handle(@Nonnull Cluster cluster) throws NullPointerException{
        Preconditions.checkNotNull(cluster);
        visit(cluster);
        traverse(cluster);
        endVisit(cluster);
    }
}
