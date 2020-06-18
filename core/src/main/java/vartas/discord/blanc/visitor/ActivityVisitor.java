package vartas.discord.blanc.visitor;

import org.jfree.chart.JFreeChart;
import vartas.chart.Interval;
import vartas.chart.line.DelegatingLineChart;
import vartas.discord.blanc.Guild;
import vartas.discord.blanc.Message;
import vartas.discord.blanc.TextChannel;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * This visitor class is responsible for creating the activity chart,
 * based on the number of {@link Message Messages} in the individual ${@link Guild Guilds}.
 * <br>
 * The number of messages that are displayed are limited by the cache size of the {@link TextChannel TextChannels}.
 * <br>
 * For readability, the chart will accumulated all {@link Message Messages} that are made within an hour. This is done
 * by truncating the minutes and seconds of the creation time.
 */
@Nonnull
public class ActivityVisitor implements ArchitectureVisitor {
    /**
     * The internal chart containing all cached messages.     *
     */
    @Nonnull
    private final DelegatingLineChart<Message> chart;

    /**
     * Initializes am empty visitor.
     * @param guild The {@link Guild} over which the chart is created.
     */
    private ActivityVisitor(@Nonnull Guild guild){
        chart = new DelegatingLineChart<>(messages -> (long)messages.size());
        chart.setTitle(guild.getName());
        chart.setXAxisLabel("Time");
        chart.setYAxisLabel("#Messages");
        chart.setInterval(Interval.HOUR);
    }

    /**
     * Creates a new {@link JFreeChart} containing the activity of the specified {@link Guild}.
     * @param guild the {@link Guild} associated with the activity chart.
     * @param textChannels A subset of {@link TextChannel TextChannels} of the specified {@link Guild}.
     *                     For those channels, the chart will also contain the individual amount of cached messages.
     * @return A {@link JFreeChart} representing the cached {@link Message Messages} in the specified {@link Guild},
     * with respect to their creating time.
     */
    @Nonnull
    public static JFreeChart create(@Nonnull Guild guild, @Nonnull List<TextChannel> textChannels){
        ActivityVisitor visitor = new ActivityVisitor(guild);

        for(TextChannel textChannel : textChannels)
            textChannel.accept(visitor);

        return visitor.chart.create();
    }

    /**
     * Adds all cached {@link Message Messages} of the {@link TextChannel} to the internal chart.
     * @param textChannel One of the {@link TextChannel TextChannels} of the {@link Guild}
     */
    @Override
    public void visit(@Nonnull TextChannel textChannel){
        for(Message message : textChannel.valuesMessages())
            chart.add(
                    textChannel.getName(),
                    LocalDateTime.ofInstant(message.getCreated(), ZoneOffset.UTC),
                    message
            );
    }
}
