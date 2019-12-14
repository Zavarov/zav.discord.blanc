package vartas.discord.bot.visitor;

import vartas.discord.bot.entities.Cluster;

public interface ClusterVisitor extends RedditFeedVisitor{
    default void visit(Cluster cluster){}

    default void traverse(Cluster cluster) {
        cluster.accept(this);
    }

    default void endVisit(Cluster cluster){}

    default void handle(Cluster cluster){
        visit(cluster);
        traverse(cluster);
        endVisit(cluster);
    }
}
