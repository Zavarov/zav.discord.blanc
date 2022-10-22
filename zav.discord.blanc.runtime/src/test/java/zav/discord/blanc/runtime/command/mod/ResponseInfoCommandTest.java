package zav.discord.blanc.runtime.command.mod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Site;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.AutoResponseEntity;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.runtime.command.AbstractDatabaseTest;

/**
 * This test case verifies whether the list of all auto-responses are displayed correctly.
 */
@ExtendWith(MockitoExtension.class)
public class ResponseInfoCommandTest extends AbstractDatabaseTest<GuildEntity> {
  @Captor ArgumentCaptor<List<Site.Page>> pages;
  GuildCommandManager manager;
  ResponseInfoCommand command;
  AutoResponseEntity responseEntity;
  
  /**
   * Initializes the command with no arguments. The database is initialized with one automatic
   * response.
   */
  @BeforeEach
  public void setUp() {
    super.setUp(new GuildEntity());
    
    responseEntity = new AutoResponseEntity();
    responseEntity.setExpression("Hello There");
    responseEntity.setAnswer("General Kenobi");
    
    when(entityManager.find(eq(GuildEntity.class), any())).thenReturn(entity);
    entity.add(responseEntity);
    
    manager = spy(new GuildCommandManager(client, event));
    command = new ResponseInfoCommand(event, manager);
    
    doNothing().when(manager).submit(pages.capture());
  }
  
  @Test
  public void testShowEmptyPage() throws Exception {
    entity.setAutoResponses(new ArrayList<>());
    
    command.run();
  
    assertTrue(pages.getValue().isEmpty());
  }
  
  @Test
  public void testShowPage() throws Exception {
    entity.setAutoResponses(new ArrayList<>(List.of(responseEntity)));
    
    command.run();
  
    assertEquals(pages.getValue().size(), 1);
  }
}
