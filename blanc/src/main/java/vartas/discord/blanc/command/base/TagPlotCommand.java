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

import net.dv8tion.jda.core.entities.Message;
import org.jfree.chart.JFreeChart;
import vartas.discord.bot.api.communicator.CommunicatorInterface;
import vartas.discord.bot.api.environment.RedditInterface;
import vartas.discord.bot.command.entity._ast.ASTEntityType;
import vartas.reddit.SubmissionInterface;
import vartas.reddit.chart.pie.TagPlot;

import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Submits a pie chart over all of the submission tags in the given time frame.
 * Those tags indicate if the post has been marked as NSFW, Spoiler or both.
 */
public class TagPlotCommand extends TagPlotCommandTOP{
    public TagPlotCommand(Message source, CommunicatorInterface communicator, List<ASTEntityType> parameters) throws IllegalArgumentException, IllegalStateException {
        super(source, communicator, parameters);
    }

    /**
     * Retrieves the data from the pushshift wrapper and creates the plot.
     */
    @Override
    public void run(){
        Instant from = super.from.toInstant();
        Instant to = super.to.toInstant();

        int width = environment.config().getImageWidth();
        int height = environment.config().getImageHeight();

        Set<SubmissionInterface> submissions = RedditInterface.loadSubmission(from, to, subreddit)
                .entries()
                .stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());

        JFreeChart chart = new TagPlot(.66).apply(submissions);
        BufferedImage image = chart.createBufferedImage(width, height);

        communicator.send(channel,image);
    }
}
