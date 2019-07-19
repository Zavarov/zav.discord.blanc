/*
 * Copyright (C) 2018 u/Zavarov
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
import com.google.common.collect.Multimaps;
import net.dv8tion.jda.core.entities.Message;
import org.jfree.chart.JFreeChart;
import vartas.discord.bot.api.communicator.CommunicatorInterface;
import vartas.discord.bot.api.environment.RedditInterface;
import vartas.discord.bot.command.entity._ast.ASTEntityType;
import vartas.reddit.SubmissionInterface;
import vartas.reddit.chart.line.AbstractChart;
import vartas.reddit.chart.line.NsfwChart;

import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.List;


/**
 * This command posts a plot containing the submissions that have been marked as NSFW in the given timeframe.
 */
public class NsfwChartCommand extends NsfwChartCommandTOP{
    public NsfwChartCommand(Message source, CommunicatorInterface communicator, List<ASTEntityType> parameters) throws IllegalArgumentException, IllegalStateException {
        super(source, communicator, parameters);
    }

    /**
     * Retrieves the data from the pushshift wrapper and creates the plot.
     */
    @Override
    public void run(){
        Instant from = fromSymbol.resolve().get().toInstant();
        Instant to = toSymbol.resolve().get().toInstant();
        String subreddit = subredditSymbol.resolve();
        AbstractChart.Interval interval = intervalSymbol.resolve().get();

        int width = environment.config().getImageWidth();
        int height = environment.config().getImageHeight();

        ListMultimap<Instant, SubmissionInterface> submissions = RedditInterface.loadSubmission(from, to, subreddit);

        JFreeChart chart = new NsfwChart().create(Multimaps.asMap(submissions), interval);
        BufferedImage image = chart.createBufferedImage(width, height);

        communicator.send(channel,image);
    }
    
}