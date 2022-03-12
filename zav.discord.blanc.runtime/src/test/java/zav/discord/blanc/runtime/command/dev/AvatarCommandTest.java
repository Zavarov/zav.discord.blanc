/*
 * Copyright (c) 2022 Zavarov.
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.managers.AccountManager;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.runtime.command.AbstractDevCommandTest;

/**
 * Checks whether the bot avatar has can be updated.
 */
@ExtendWith(MockitoExtension.class)
public class AvatarCommandTest  extends AbstractDevCommandTest {
  
  private @Mock AccountManager manager;
  private @Mock CompletableFuture<InputStream> completableFuture;
  private @Mock InputStream is;
  private @Mock Icon icon;
  private @Mock MessageAction action;
  private MockedStatic<Icon> mocked;
  
  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    
    mocked = mockStatic(Icon.class);
    mocked.when(() -> Icon.from(any(InputStream.class))).thenReturn(icon);
  }
  
  @Override
  @AfterEach
  public void tearDown() throws Exception {
    super.tearDown();
    
    mocked.close();
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    check("b:dev.avatar", AvatarCommand.class);
  }
  
  @Test
  public void testSetAvatar() throws Exception {
    when(attachment.retrieveInputStream()).thenReturn(completableFuture);
    when(completableFuture.get()).thenReturn(is);
    when(shard.getSelfUser()).thenReturn(selfUser);
    when(selfUser.getManager()).thenReturn(manager);
    when(manager.setAvatar(any(Icon.class))).thenReturn(manager);
    when(textChannel.sendMessage(any(CharSequence.class))).thenReturn(action);
    
    run("b:dev.avatar");
    
    verify(manager, times(1)).setAvatar(any());
  }
}
