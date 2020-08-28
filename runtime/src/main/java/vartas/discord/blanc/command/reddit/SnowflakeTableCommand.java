package vartas.discord.blanc.command.reddit;

import com.google.common.collect.Range;
import vartas.discord.blanc.MessageEmbed;
import vartas.discord.blanc.factory.MessageEmbedFactory;
import vartas.reddit.Comment;
import vartas.reddit.Submission;
import vartas.reddit.Subreddit;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.Map;

import static vartas.discord.blanc.Main.REDDIT_CLIENT;

public class SnowflakeTableCommand extends SnowflakeTableCommandTOP implements SnowflakeCommand{
    private static final int size = 10;

    @Override
    public void run() {
        Instant inclusiveFrom = getFrom().atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant exclusiveTo = getTo().atStartOfDay(ZoneOffset.UTC).toInstant();
        Range<Instant> range = Range.closedOpen(inclusiveFrom, exclusiveTo);

        Subreddit subreddit = REDDIT_CLIENT.getUncheckedSubreddits(getSubreddit());

        switch(getType().toLowerCase(Locale.ENGLISH)){
            case "submission":
                printTopSubmissions(subreddit, range);
                break;
            case "submitter":
                printTopSubmitters(subreddit, range);
                break;
            case "comment":
                printTopComments(subreddit, range);
                break;
            case "commenter":
                printTopCommenters(subreddit, range);
                break;
            case "core":
                printCore(subreddit, range);
                break;
            default:
                throw new IllegalArgumentException(type + " is not a valid type.");
        }
    }

    private void printTopSubmissions(Subreddit subreddit, Range<Instant> range){
        MessageEmbed messageEmbed = MessageEmbedFactory.create();
        for(Submission submission : getTopSubmissions(subreddit, range, size)){
            String title = String.format("[%d] by u/%s", submission.getScore(), submission.getAuthor());
            String content = String.format("[%s](%s)", submission.getTitle(), submission.getShortLink());
            messageEmbed.addFields(title, content);
        }
        get$MessageChannel().send(messageEmbed);
    }

    private void printTopComments(Subreddit subreddit, Range<Instant> range){
        MessageEmbed messageEmbed = MessageEmbedFactory.create();
        for(Comment comment : getTopComments(subreddit, range, size)){
            String title = String.format("[%d] by u/%s", comment.getScore(), comment.getAuthor());
            String content = String.format("[%s](%s)", comment.getSubmission().getTitle(), comment.getPermaLink());
            messageEmbed.addFields(title, content);
        }
        get$MessageChannel().send(messageEmbed);
    }

    private void printTopSubmitters(Subreddit subreddit, Range<Instant> range){
        MessageEmbed messageEmbed = MessageEmbedFactory.create();
        for(Map.Entry<String, LimitedSubmissionSet> entry : getTopSubmitters(subreddit, range, size)){
            String title = String.format("u/%s [%d]", entry.getKey(), entry.getValue().getScore());
            for(Submission submission : entry.getValue()){
                String content = String.format("[%s](%s)[%d]", submission.getTitle(), submission.getShortLink(), submission.getScore());
                messageEmbed.addFields(title, content);
                //Only print the first submission to keep it short
                break;
            }
        }
        get$MessageChannel().send(messageEmbed);
    }

    private void printTopCommenters(Subreddit subreddit, Range<Instant> range){
        MessageEmbed messageEmbed = MessageEmbedFactory.create();
        for(Map.Entry<String, LimitedCommentSet> entry : getTopCommenters(subreddit, range, size)){
            String title = String.format("u/%s [%d]", entry.getKey(), entry.getValue().getScore());
            for(Comment comment : entry.getValue()){
                String content = String.format("[%s](%s)[%d]", comment.getSubmission().getTitle(), comment.getPermaLink(), comment.getScore());
                messageEmbed.addFields(title, content);
                //Only print the first comment to keep it short
                break;
            }
        }
        get$MessageChannel().send(messageEmbed);
    }

    private void printCore(Subreddit subreddit, Range<Instant> range){
        MessageEmbed messageEmbed = MessageEmbedFactory.create();
        messageEmbed.addFields("#Submissions", countSubmissions(subreddit, range));
        messageEmbed.addFields("#Comments", countComments(subreddit, range));
        messageEmbed.addFields("#Unique Submitters", countUniqueSubmitters(subreddit, range));
        messageEmbed.addFields("#Unique Commenters", countUniqueCommenters(subreddit, range));
        messageEmbed.addFields("Total Submission Score", countTotalSubmissionScore(subreddit, range));
        messageEmbed.addFields("Total Comment Score", countTotalCommentScore(subreddit, range));
        messageEmbed.addFields("#Submissions/day", countSubmissionsPerDay(subreddit, range));
        messageEmbed.addFields("#Comments/day", countCommentsPerDay(subreddit, range));
        get$MessageChannel().send(messageEmbed);
    }
}
