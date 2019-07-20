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
import vartas.discord.bot.command.entity._ast.ASTEntityType;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

/**
 * This command generates a plot of the activity in the respective guild.
 */
public class ActivityCommand extends ActivityCommandTOP{
    public ActivityCommand(Message source, CommunicatorInterface communicator, List<ASTEntityType> parameters) throws IllegalArgumentException, IllegalStateException {
        super(source, communicator, parameters);
    }

    @Override
    public void run() {
        int width = environment.config().getImageWidth();
        int height = environment.config().getImageHeight();

        JFreeChart chart = communicator.activity(guild, Collections.emptySet());
        BufferedImage image = chart.createBufferedImage(width, height);

        communicator.send(channel,image);
    }
}
