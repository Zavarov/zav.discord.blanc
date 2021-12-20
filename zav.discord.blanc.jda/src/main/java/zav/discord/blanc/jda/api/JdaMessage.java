/*
 * Copyright (c) 2021 Zavarov.
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

package zav.discord.blanc.jda.api;

import static zav.discord.blanc.jda.internal.GuiceUtils.injectShard;
import static zav.discord.blanc.jda.internal.GuiceUtils.injectUser;

import com.google.inject.Injector;
import javax.inject.Inject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import zav.discord.blanc.databind.MessageValueObject;

/**
 * Implementation of a message view, backed by JDA.
 */
public abstract class JdaMessage implements zav.discord.blanc.api.Message {
  @Inject
  private Injector injector;
  
  @Inject
  protected Message jdaMessage;
  
  @Override
  public MessageValueObject getAbout() {
    return new MessageValueObject()
          .withId(jdaMessage.getIdLong())
          // TODO
          .withAttachment(null)
          .withAuthor(jdaMessage.getAuthor().getName())
          .withContent(jdaMessage.getContentRaw())
          .withAuthorId(jdaMessage.getAuthor().getIdLong());
  }
  
  @Override
  public JdaUser getAuthor() {
    User jdaUser = jdaMessage.getAuthor();
  
    return injectUser(injector, jdaUser);
  }
  
  @Override
  public JdaShard getShard() {
    JDA jda = jdaMessage.getJDA();
  
    return injectShard(injector, jda);
  }
  
  @Override
  public void delete() {
    jdaMessage.delete().complete();
  }
  
  @Override
  public void react(String reaction) {
    jdaMessage.addReaction(reaction).complete();
  }
}
