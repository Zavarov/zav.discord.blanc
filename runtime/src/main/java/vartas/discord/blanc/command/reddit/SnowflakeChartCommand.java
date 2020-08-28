package vartas.discord.blanc.command.reddit;

import com.google.common.collect.Range;
import net.sourceforge.plantuml.StringUtils;
import vartas.chart.Interval;
import vartas.chart.line.DelegatingLineChart;
import vartas.reddit.Subreddit;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Locale;

import static vartas.discord.blanc.Main.REDDIT_CLIENT;

public class SnowflakeChartCommand extends SnowflakeChartCommandTOP implements SnowflakeCommand{
    private static final int width = 800;
    private static final int height = 600;

    private Subreddit subreddit;
    private LocalDateTime inclusiveFrom;
    private LocalDateTime inclusiveTo;

    @Override
    public void run() {
        DelegatingLineChart<Long> chart = new DelegatingLineChart<>((u, v) -> v.stream().mapToLong(x -> x).sum());

        chart.setTitle(String.format("Snowflake Chart over r/%s (per %s)", subreddit, StringUtils.capitalize(interval.name().toLowerCase())));
        chart.setYAxisLabel("Count");
        chart.setXAxisLabel("Time");
        chart.setInterval(interval);

        inclusiveFrom = getFrom().atStartOfDay(ZoneOffset.UTC).toLocalDateTime();
        inclusiveTo = getTo().atStartOfDay(ZoneOffset.UTC).minusDays(1).toLocalDateTime();
        subreddit = REDDIT_CLIENT.getUncheckedSubreddits(getSubreddit());

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

        get$MessageChannel().send(chart.create().createBufferedImage(width, height));
    }

    private void addSubmissions(DelegatingLineChart<Long> chart){
        Interval.DAY.getDates(inclusiveFrom, inclusiveTo, 1).forEach(day -> {
            Range<Instant> range = Range.closedOpen(
                    day.toInstant(ZoneOffset.UTC),
                    day.plusDays(1).toInstant(ZoneOffset.UTC)
            );

            chart.add("#Submissions", day, countSubmissions(subreddit, range));
            chart.add("#NSFW Submissions", day, countNsfwSubmissions(subreddit, range));
            chart.add("#Spoiler Submissions", day, countSpoilerSubmissions(subreddit, range));
        });
    }

    private void addComments(DelegatingLineChart<Long> chart){
        Interval.DAY.getDates(inclusiveFrom, inclusiveTo, 1).forEach(day -> {
            Range<Instant> range = Range.closedOpen(
                    day.toInstant(ZoneOffset.UTC),
                    day.plusDays(1).toInstant(ZoneOffset.UTC)
            );

            chart.add("#Comments", day, countComments(subreddit, range));
        });
    }

    private void addSubmitters(DelegatingLineChart<Long> chart){
        Interval.DAY.getDates(inclusiveFrom, inclusiveTo, 1).forEach(day -> {
            Range<Instant> range = Range.closedOpen(
                    day.toInstant(ZoneOffset.UTC),
                    day.plusDays(1).toInstant(ZoneOffset.UTC)
            );

            chart.add("#Unique Submitters", day, countUniqueSubmitters(subreddit, range));
        });
    }

    private void addCommenters(DelegatingLineChart<Long> chart){
        Interval.DAY.getDates(inclusiveFrom, inclusiveTo, 1).forEach(day -> {
            Range<Instant> range = Range.closedOpen(
                    day.toInstant(ZoneOffset.UTC),
                    day.plusDays(1).toInstant(ZoneOffset.UTC)
            );

            chart.add("#Unique Submitters", day, countUniqueCommenters(subreddit, range));
        });
    }
}
