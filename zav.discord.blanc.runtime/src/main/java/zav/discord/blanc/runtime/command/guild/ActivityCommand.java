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
package zav.discord.blanc.runtime.command.guild;

import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.Argument;
import zav.discord.blanc.databind.TextChannel;
import zav.discord.blanc.view.TextChannelView;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This command generates a plot of the activity in the respective guild.
 */
public class ActivityCommand extends AbstractGuildCommand {
  private List<TextChannel> myChannels;
  
  @Override
  public void postConstruct(List<? extends Argument> args) {
    myChannels = args.stream()
          .map(guild::getTextChannel)
          .map(TextChannelView::getAbout)
          .collect(Collectors.toUnmodifiableList());
  }

  @Override
  public void run() throws IOException {
    BufferedImage image = guild.getActivity(myChannels);
    channel.send(image, "Activity");
  }
}
