/*
 * Copyright (c) 2020 Zavarov
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

package vartas.discord.blanc.command.reddit;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import org.apache.http.client.HttpResponseException;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vartas.chart.pie.DelegatingPieChart;
import vartas.reddit.JSONSubreddit;
import vartas.reddit.Submission;
import vartas.reddit.Subreddit;
import vartas.reddit.UnsuccessfulRequestException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;

import static vartas.discord.blanc.Main.REDDIT_CLIENT;

public class SubmissionCommand extends SubmissionCommandTOP {
    private static final double alpha = 0.66;
    private static final int width = 800;
    private static final int height = 600;

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final DiscreteDomain<LocalDate> domain = new JSONSubreddit.DiscreteLocalDateDomain();
    private Subreddit subreddit;
    private Range<LocalDate> range;

    @Override
    public void run() {
        subreddit = REDDIT_CLIENT.getUncheckedSubreddits(getSubreddit());
        range = Range.closedOpen(getFrom(), getTo());

        try {
            JFreeChart chart;
            switch (type.toLowerCase(Locale.ENGLISH)) {
                case "tag":
                    chart = createTagChart();
                    break;
                case "flair":
                    chart = createFlairChart();
                    break;
                default:
                    throw new IllegalArgumentException(type + " is not a valid type.");
            }

            BufferedImage image = chart.createBufferedImage(width, height);
            send$MessageChannel(image, chart.getTitle().getText());
        }catch(HttpResponseException | UnsuccessfulRequestException e){
            throw new IllegalStateException(e);
        }
    }

    private JFreeChart createTagChart() throws UnsuccessfulRequestException, HttpResponseException {
        DelegatingPieChart<Submission> chart = new DelegatingPieChart<>((label, submission) -> (long)submission.size());

        for(LocalDate date : ContiguousSet.create(range, domain)){
            Instant inclusiveFrom = date.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant exclusiveTo = domain.next(date).atStartOfDay(ZoneOffset.UTC).toInstant();

            log.info("Requesting submissions over [{}, {})", inclusiveFrom, exclusiveTo);
            log.info(subreddit.getClass().toString());

            List<Submission> submissions = subreddit.getSubmissions(inclusiveFrom, exclusiveTo);

            for(Submission submission : submissions){
                if(submission.getNsfw() && submission.getSpoiler())
                    chart.add("both", submission);
                else if(submission.getNsfw())
                    chart.add("NSFW", submission);
                else if(submission.getSpoiler())
                    chart.add("Spoiler", submission);
                else
                    chart.add("untagged", submission);
            }
        }

        chart.setAlpha(alpha);
        chart.setTitle("Submission tags over r/"+getSubreddit());

        JFreeChart jchart = chart.create();

        //Manually recolor the sections to make them prettier.
        PiePlot plot = (PiePlot)jchart.getPlot();
        plot.setSectionPaint("untagged", new Color(119,119,119,(int)(225*alpha)));
        plot.setSectionPaint("NSFW", new Color(255,0,0,(int)(225*alpha)));
        plot.setSectionPaint("Spoiler", new Color(0,0,0,(int)(225*alpha)));
        plot.setSectionPaint("both", blend(Color.RED,Color.BLACK, alpha));

        return jchart;
    }

    /**
     * C = A*alpha - (1-alpha)*B
     */
    private static Color blend(Color A, Color B, double alpha){
        int r = (int)(A.getRed() * alpha + (1-alpha) * B.getRed());
        int b = (int)(A.getBlue() * alpha + (1-alpha) * B.getBlue());
        int g = (int)(A.getGreen()* alpha + (1-alpha) * B.getGreen());
        return new Color(r,g,b,(int)(alpha*255));
    }

    private JFreeChart createFlairChart() throws UnsuccessfulRequestException, HttpResponseException {
        DelegatingPieChart<Submission> chart = new DelegatingPieChart<>((label, submission) -> (long)submission.size());

        for(LocalDate date : ContiguousSet.create(range, domain)){
            Instant inclusiveFrom = date.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant exclusiveTo = domain.next(date).atStartOfDay(ZoneOffset.UTC).toInstant();

            log.info("Requesting submissions over ({}, {}]", inclusiveFrom, exclusiveTo);

            List<Submission> submissions = subreddit.getSubmissions(inclusiveFrom, exclusiveTo);

            for(Submission submission : submissions){
                String label;
                label = submission.getLinkFlairText().orElse("unflaired");
                label = label.isBlank() ? "unflaired" : label;
                chart.add(label, submission);
            }
        }

        chart.setAlpha(1.0);
        chart.setTitle("Submission flairs over r/"+getSubreddit());

        return chart.create();
    }
}
