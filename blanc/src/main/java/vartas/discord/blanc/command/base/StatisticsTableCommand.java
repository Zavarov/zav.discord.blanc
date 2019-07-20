/*
 * Copyright (c) 2019 Zavarov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package vartas.discord.blanc.command.base;

import com.google.common.collect.ListMultimap;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import org.atteo.evo.inflector.English;
import vartas.discord.bot.api.communicator.CommunicatorInterface;
import vartas.discord.bot.api.environment.RedditInterface;
import vartas.discord.bot.api.message.InteractiveMessage;
import vartas.discord.bot.command.entity._ast.ASTEntityType;
import vartas.reddit.CommentInterface;
import vartas.reddit.SubmissionInterface;
import vartas.reddit.stats.*;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

/**
 * This command creates a table containing the overall information about the
 * comments and submittions, as well as the top comments, commenter, submissions
 * and submitter.
 */
public class StatisticsTableCommand extends StatisticsTableCommandTOP{
    /**
     * All comments within that interval.
     */
    protected Collection<CommentInterface> comments;
    /**
     * All submissions within that interval.
     */
    protected Collection<SubmissionInterface> submissions;
    /**
     * The number of days that have been requested.
     */
    protected long days;

    public StatisticsTableCommand(Message source, CommunicatorInterface communicator, List<ASTEntityType> parameters) throws IllegalArgumentException, IllegalStateException {
        super(source, communicator, parameters);
    }

    /**
     * @param name the name of a Reddit user.
     * @return a hidden link to the Reddit user.
     */
    private String getUser(String name){
        return String.format("[%s](https://www.reddit.com/user/%s)",name,name);
    }
    /**
     * @param title the title of the url
     * @param link a Reddit link.
     * @return a hidden link to the provided url
     */
    private String getLink(String title, String link){
        return String.format("[%s](%s)",title,link);
    }
    /**
     * Adds the accumulated data of the comments to the builder.
     * @param message the underlying builder.
     */
    private void addAccumulatedComment(InteractiveMessage.Builder message){
        AccumulatedComment comment = new AccumulatedComment(comments,days);
        
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor("Comments");
        embed.addField("Total Count", Long.toString(comment.getTotal()), false);
        embed.addField("Rate (per day)", String.format("%.2f",comment.getRate()), false);
        embed.addField("Unique Redditors", Long.toString(comment.getUniqueCommenters()), false);
        embed.addField("Combined Score", Long.toString(comment.getCombinedScore()), false);
        message.addPage(embed);
    }
    /**
     * Adds the accumulated data of the submissions to the builder.
     * @param message the underlying builder.
     */
    private void addAccumulatedSubmission(InteractiveMessage.Builder message){
        AccumulatedSubmission submission = new AccumulatedSubmission(submissions,days);
        
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor("Submissions");
        embed.addField("Total Count", Long.toString(submission.getTotal()), false);
        embed.addField("Rate (per day)", String.format("%.2f",submission.getRate()), false);
        embed.addField("Unique Redditors", Long.toString(submission.getUniqueSubmitters()), false);
        embed.addField("Combined Score", Long.toString(submission.getCombinedScore()), false);
        message.addPage(embed);
    }
    /**
     * Adds the top ten comments to the message.
     * @param message the underlying builder.
     */
    private void addTopComment(InteractiveMessage.Builder message){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor("Top Comments");
        new TopComment().compute(comments, 10).forEach(c -> {
            embed.appendDescription("In ")
                    .appendDescription(getLink(c.getSubmissionTitle(),c.getPermalink()))
                    .appendDescription("\n");
            embed.appendDescription(String.format("`%d` %s, by %s\n",
                    c.getScore(),
                    English.plural("point", c.getScore()),
                    getUser(c.getAuthor())));
        });
        message.addPage(embed);
    }
    /**
     * Adds the top ten commenters to the message.
     * @param message the underlying builder.
     */
    private void addTopCommenter(InteractiveMessage.Builder message){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor("Top Commenters");
        new TopCommenter().compute(comments, 10).forEach(e -> {
            int score = e.getMiddle();
            int size = e.getRight().size();
            embed.appendDescription(getUser(e.getLeft())).appendDescription("\n");
            embed.appendDescription(String.format("`%d` %s, `%d` %s\n",
                    score,
                    English.plural("point", score),
                    size,
                    English.plural("comment",size)));
        });
        message.addPage(embed);
    }
    /**
     * Adds the top ten submissions to the message.
     * @param message the underlying builder.
     */
    private void addTopSubmission(InteractiveMessage.Builder message){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor("Top Submissions");
        new TopSubmission().compute(submissions, 10).forEach(s -> {
            embed.appendDescription(getLink(s.getTitle(),s.getPermalink())).appendDescription("\n");
            embed.appendDescription(String.format("`%d` by %s\n",s.getScore(),getUser(s.getAuthor())));
        });
        message.addPage(embed);
    }
    /**
     * Adds the top ten submitter and their top 10 submissions to the message.
     * @param message the underlying builder.
     */
    private void addTopSubmitter(InteractiveMessage.Builder message){
        new TopSubmitter().compute(submissions, 10).forEach(e -> {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor("Top Submitter");
            embed.appendDescription(getUser(e.getLeft())).appendDescription("\n\n");
            e.getRight().stream().limit(10).forEach(s -> {
                embed.appendDescription(String.format("`%d`, %s\n",s.getScore(),getLink(s.getTitle(),s.getPermalink())));
            });
            message.addPage(embed);
        });
    }
    /**
     * Creates an interactive message containing all tables.
     */
    @Override
    public void run(){
        InteractiveMessage.Builder builder = new InteractiveMessage.Builder(channel, author, communicator);

        Instant from = fromSymbol.resolve().get().toInstant();
        Instant to = toSymbol.resolve().get().toInstant();
        String subreddit = subredditSymbol.resolve();

        ListMultimap<Instant, CommentInterface> commentMap = RedditInterface.loadComment(from, to, subreddit);
        ListMultimap<Instant, SubmissionInterface> submissionMap = RedditInterface.loadSubmission(from, to, subreddit);

        days = RedditInterface.countDays(from, to);
        comments = commentMap.values();
        submissions = submissionMap.values();
        
        addAccumulatedSubmission(builder);
        addAccumulatedComment(builder);
        addTopComment(builder);
        addTopCommenter(builder);
        addTopSubmission(builder);
        addTopSubmitter(builder);
        
        communicator.send(builder.build());
    }
}