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

import java.io.InputStream;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.lang3.Validate;
import org.eclipse.jdt.annotation.NonNullByDefault;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.command.AbstractCommand;

/**
 * This command changes the avatar of the bot to the one that was attached to
 * the message that executed this command.
 */
@NonNullByDefault
public class AvatarCommand extends AbstractCommand {
  
  public AvatarCommand() {
    super(Rank.DEVELOPER);
  }
  
  @Override
  public void postConstruct() {
    Validate.validIndex(message.getAttachments(), 0, i18n.getString("missing_image"));
  }
  
  @Override
  public void run() throws Exception {
    Message.Attachment attachment = message.getAttachments().get(0);
    
    try (InputStream is = attachment.retrieveInputStream().get()) {
      shard.getSelfUser().getManager().setAvatar(Icon.from(is)).complete();
      channel.sendMessage(i18n.getString("avatar_updated")).complete();
    }
  }
}
