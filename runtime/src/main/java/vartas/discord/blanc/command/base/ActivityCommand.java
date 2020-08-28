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
package vartas.discord.blanc.command.base;

import org.jfree.chart.JFreeChart;
import vartas.discord.blanc.TextChannel;
import vartas.discord.blanc.visitor.ActivityVisitor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This command generates a plot of the activity in the respective guild.
 */
public class ActivityCommand extends ActivityCommandTOP{
    @Override
    public void run(){
        JFreeChart chart = ActivityVisitor.create(get$Guild(), new ArrayList<>(get$Guild().valuesChannels()));
        BufferedImage image = chart.createBufferedImage(1024, 768);
        send$TextChannel(image, chart.getTitle().getText());
    }
}
