package vartas.discord.blanc.command.reddit;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import net.sourceforge.plantuml.StringUtils;
import vartas.chart.Interval;
import vartas.chart.line.DelegatingLineChart;
import vartas.reddit.JSONSubreddit;
import vartas.reddit.Subreddit;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Locale;

import static vartas.discord.blanc.Main.REDDIT_CLIENT;

public class SubredditCommand extends SubredditCommandTOP implements SnowflakeCommand{
    private static final int width = 800;
    private static final int height = 600;

    private DiscreteDomain<LocalDate> domain = new JSONSubreddit.DiscreteLocalDateDomain();
    private Range<LocalDate> range;
    private Subreddit subreddit;

    @Override
    public void run() {
        subreddit = REDDIT_CLIENT.getUncheckedSubreddits(getSubreddit());
        range = Range.closedOpen(getFrom(), getTo());

        DelegatingLineChart<Long> chart = new DelegatingLineChart<>((u, v) -> v.stream().mapToLong(x -> x).sum());

        chart.setTitle(String.format("Snowflake Chart over r/%s (per %s)", subreddit.getName(), interval.name().toLowerCase()));
        chart.setYAxisLabel("Count");
        chart.setXAxisLabel("Time");
        chart.setInterval(interval);

        for(String flag : get$Flags()){
            switch(flag.toLowerCase(Locale.ENGLISH)){
                case "submission":
                    addSubmissions(chart);
                    break;
                case "submitter":
                    addSubmitters(chart);
                    break;
                case "comment":
                    addComments(chart);
                    break;
                case "commenter":
                    addCommenters(chart);
                    break;
                default:
                    throw new IllegalArgumentException(flag + " is not a valid flag.");
            }
        }

        get$MessageChannel().send(chart.create().createBufferedImage(width, height), getSubreddit()+".png");
    }

    private void addSubmissions(DelegatingLineChart<Long> chart){
        for(LocalDate date : ContiguousSet.create(range, domain)) {
            Instant inclusiveFrom = date.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant exclusiveTo = domain.next(date).atStartOfDay(ZoneOffset.UTC).toInstant();
            Range<Instant> range = Range.closedOpen(inclusiveFrom, exclusiveTo);
            LocalDateTime day = date.atStartOfDay();

            chart.add("#Submissions", day, countSubmissions(subreddit, range));
            chart.add("#NSFW Submissions", day, countNsfwSubmissions(subreddit, range));
            chart.add("#Spoiler Submissions", day, countSpoilerSubmissions(subreddit, range));
        }
    }

    private void addComments(DelegatingLineChart<Long> chart){
        for(LocalDate date : ContiguousSet.create(range, domain)) {
            Instant inclusiveFrom = date.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant exclusiveTo = domain.next(date).atStartOfDay(ZoneOffset.UTC).toInstant();
            Range<Instant> range = Range.closedOpen(inclusiveFrom, exclusiveTo);
            LocalDateTime day = date.atStartOfDay();

            chart.add("#Comments", day, countComments(subreddit, range));
        }
    }

    private void addSubmitters(DelegatingLineChart<Long> chart){
        for(LocalDate date : ContiguousSet.create(range, domain)) {
            Instant inclusiveFrom = date.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant exclusiveTo = domain.next(date).atStartOfDay(ZoneOffset.UTC).toInstant();
            Range<Instant> range = Range.closedOpen(inclusiveFrom, exclusiveTo);
            LocalDateTime day = date.atStartOfDay();

            chart.add("#Unique Submitters", day, countUniqueSubmitters(subreddit, range));
        }
    }

    private void addCommenters(DelegatingLineChart<Long> chart){
        for(LocalDate date : ContiguousSet.create(range, domain)) {
            Instant inclusiveFrom = date.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant exclusiveTo = domain.next(date).atStartOfDay(ZoneOffset.UTC).toInstant();
            Range<Instant> range = Range.closedOpen(inclusiveFrom, exclusiveTo);
            LocalDateTime day = date.atStartOfDay();

            chart.add("#Unique Commenters", day, countUniqueCommenters(subreddit, range));
        }
    }
}
