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

package zav.discord.blanc.runtime.command.dev;

import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.command.Rank;
import zav.discord.blanc.command.AbstractCommand;
import zav.discord.blanc.databind.MessageDto;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

/**
 * This command changes the avatar of the bot to the one that was attached to
 * the message that executed this command.
 */
public class AvatarCommand extends AbstractCommand {
  private MessageDto myMessageData;
  
  public AvatarCommand() {
    super(Rank.DEVELOPER);
  }
  
  @Override
  public void postConstruct(List<? extends Argument> args) {
    myMessageData = message.getAbout();
  }
  
  @Override
  public void run() throws IOException {
    @Nullable String attachment = myMessageData.getAttachment().orElse(null);
    
    if (attachment == null) {
      channel.send("Please attach an image to the command.");
      return;
    }
    
    byte[] data = Base64.getDecoder().decode(attachment);
    try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
      BufferedImage image = ImageIO.read(in);
      
      shard.getSelfUser().setAvatar(image);
      channel.send("Avatar updated.");
    }
  }
}
