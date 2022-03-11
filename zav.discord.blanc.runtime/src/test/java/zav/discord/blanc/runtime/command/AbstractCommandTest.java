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

package zav.discord.blanc.runtime.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static zav.discord.blanc.api.Constants.PATTERN;
import static zav.discord.blanc.api.Constants.SITE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.api.Parser;
import zav.discord.blanc.api.Site;
import zav.discord.blanc.databind.io.CredentialsEntity;
import zav.discord.blanc.db.Table;
import zav.discord.blanc.db.sql.SqlQuery;
import zav.discord.blanc.mc.MontiCoreCommandParser;
import zav.discord.blanc.runtime.internal.BlancModule;
import zav.discord.blanc.runtime.internal.CommandResolver;

/**
 * Base class for all JUnit command tests.
 */
public abstract class AbstractCommandTest {
  
  protected static final long OWNER_ID = Long.MAX_VALUE;
  
  protected @Mock User author;
  protected @Mock SelfUser selfUser;
  protected @Mock Guild guild;
  protected @Mock Member member;
  protected @Mock TextChannel textChannel;
  protected @Mock JDA shard;
  protected @Mock Message.Attachment attachment;
  protected @Mock Client client;
  protected @Mock zav.jrc.client.Client redditClient;
  
  protected Cache<Message, Site> siteCache;
  protected Cache<Long, Pattern> patternCache;
  
  protected CredentialsEntity credentials;
  protected Injector injector;
  
  @BeforeEach
  public void setUp() throws Exception {
    CommandResolver.init();
  
    ObjectMapper om = new ObjectMapper();
    InputStream is = getClass().getResourceAsStream("/Credentials.json");
  
    siteCache = CacheBuilder.newBuilder().build();
    patternCache = CacheBuilder.newBuilder().build();
    credentials = om.readValue(is, CredentialsEntity.class);
    injector = Guice.createInjector(new BlancModule(credentials), new TestModule());
  }
  
  @AfterEach
  public void tearDown() throws Exception {
    Files.deleteIfExists(SqlQuery.ENTITY_DB_PATH);
    Files.deleteIfExists(SqlQuery.ENTITY_DB_PATH.getParent());
  }
  
  
  // -------------------------------------------------------------------------------------------- //
  
  protected <T> T get(Table<T> db, Object... keys) throws SQLException {
    List<T> response = db.get(keys);
    assertThat(response).hasSize(1);
    return response.get(0);
  }
  
  // -------------------------------------------------------------------------------------------- //
  
  public Command parse(String content) {
    Message message = mock(Message.class);
    when(message.getJDA()).thenReturn(shard);
    
    when(message.getChannel()).thenReturn(textChannel);
    when(message.getAuthor()).thenReturn(author);
    when(message.getChannel()).thenReturn(textChannel);
    
    when(message.getGuild()).thenReturn(guild);
    when(message.getTextChannel()).thenReturn(textChannel);
    when(message.getMember()).thenReturn(member);
    
    when(message.getContentRaw()).thenReturn(content);
    
    lenient().when(message.getAttachments()).thenReturn(List.of(attachment));
    
    return parse(message);
  }
  
  private Command parse(Message message) {
    GuildMessageReceivedEvent event = mock(GuildMessageReceivedEvent.class);
    when(event.getMessage()).thenReturn(message);
    return parse(event);
  }
  
  private Command parse(GuildMessageReceivedEvent event) {
    try {
      Parser parser = injector.getInstance(MontiCoreCommandParser.class);
      
      Command command = parser.parse(event).orElseThrow();
      command.postConstruct();
      return command;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  // -------------------------------------------------------------------------------------------- //
  
  protected <T extends Command> void check(String source, Class<T> clazz) {
    assertThat(parse(source)).isInstanceOf(clazz);
  }
  
  protected void run(String format, Object... args) throws Exception {
    run(String.format(format, args));
  }
  
  protected void run(String source) throws Exception {
    parse(source).run();
  }
  
  // -------------------------------------------------------------------------------------------- //
  
  private class TestModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(Client.class).toInstance(client);
      bind(zav.jrc.client.Client.class).toInstance(redditClient);
  
      bind(new TypeLiteral<Cache<Message, Site>>(){})
            .annotatedWith(Names.named(SITE))
            .toInstance(siteCache);
  
  
      bind(new TypeLiteral<Cache<Long, Pattern>>(){})
            .annotatedWith(Names.named(PATTERN))
            .toInstance(patternCache);
    }
  }
}
