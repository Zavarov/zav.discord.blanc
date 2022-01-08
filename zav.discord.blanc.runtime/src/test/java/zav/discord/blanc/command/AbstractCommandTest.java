package zav.discord.blanc.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.argThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentMatcher;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.api.Guild;
import zav.discord.blanc.api.GuildMessage;
import zav.discord.blanc.api.Member;
import zav.discord.blanc.api.Role;
import zav.discord.blanc.api.SelfMember;
import zav.discord.blanc.api.SelfUser;
import zav.discord.blanc.api.Shard;
import zav.discord.blanc.api.TextChannel;
import zav.discord.blanc.api.WebHook;
import zav.discord.blanc.command.parser.Parser;
import zav.discord.blanc.databind.GuildValueObject;
import zav.discord.blanc.databind.MessageValueObject;
import zav.discord.blanc.databind.RoleValueObject;
import zav.discord.blanc.databind.TextChannelValueObject;
import zav.discord.blanc.databind.UserValueObject;
import zav.discord.blanc.databind.WebHookValueObject;
import zav.discord.blanc.db.GuildDatabase;
import zav.discord.blanc.db.RoleDatabase;
import zav.discord.blanc.db.TextChannelDatabase;
import zav.discord.blanc.db.UserDatabase;
import zav.discord.blanc.db.WebHookDatabase;
import zav.discord.blanc.mc.MontiCoreCommandParser;
import zav.discord.blanc.reddit.SubredditObservable;
import zav.discord.blanc.runtime.internal.CommandResolver;
import zav.jrc.client.Client;

/**
 * Base class for all JUnit command tests.
 */
public abstract class AbstractCommandTest {
  protected UserValueObject userValueObject;
  protected UserValueObject selfUserValueObject;
  protected RoleValueObject roleValueObject;
  protected GuildValueObject guildValueObject;
  protected TextChannelValueObject channelValueObject;
  protected MessageValueObject messageValueObject;
  protected WebHookValueObject webHookValueObject;
  
  protected SelfUser selfUser;
  protected Guild guild;
  protected Member member;
  protected SelfMember selfMemberView;
  protected TextChannel channelView;
  protected Shard shard;
  protected Role role;
  protected GuildMessage messageView;
  protected WebHook webHook;
  
  protected static final long guildId = 10000L;
  protected static final String guildPrefix = "myPrefix";
  protected static final String guildName = "myGuild";
  
  protected static final String roleGroup = "myGroup";
  protected static final long roleId = 20000L;
  protected static final String roleName = "myRole";
  
  protected static final long channelId = 100L;
  protected static final String channelName = "myChannel";
  protected static final String channelSubreddit = "myChannelSubreddit";
  
  protected static final long userId = 31111L;
  protected static final String userName = "myUser";
  protected static final long userDiscriminator = 1111L;
  
  protected static final long selfUserId = 32222L;
  protected static final String selfUserName = "mySelfUser";
  protected static final long selfUserDiscriminator = 2222L;
  
  protected static final String messageContent = "myContent";
  protected static final long messageId = 44444L;
  protected static final String messageAttachment = "myAttachment";
  protected static final String messageAuthor = "myUser";
  protected static final long messageAuthorId = 33333L;

  protected static final long webHookId = 55555L;
  protected static final String webHookName = "myWebHook";
  protected static final long webHookChannelId = 56666L;
  protected static final String webHookSubreddit = "myHookSubreddit";
  protected static final boolean webHookOwner = true;
  
  protected static final String url = "https://foo";
  
  private static Parser parser;
  private static final Path GUILD_DB = Paths.get("Guild.db");
  private static final Path ROLE_DB = Paths.get("Role.db");
  private static final Path CHANNEL_DB = Paths.get("TextChannel.db");
  private static final Path WEBHOOK_DB = Paths.get("WebHook.db");
  private static final Path USER_DB = Paths.get("User.db");
  
  @BeforeAll
  public static void setUpAll() {
    
    Injector injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(Client.class).toInstance(mock(Client.class));
        bind(String.class).annotatedWith(Names.named("inviteSupportServer")).toInstance(url);
      }
    });
  
    CommandResolver.init();
    SubredditObservable.init(injector);
    
    parser = injector.getInstance(MontiCoreCommandParser.class);
  }
  
  @AfterAll
  public static void tearDownAll() {
    SubredditObservable.init(null);
  }
  
  // -------------------------------------------------------------------------------------------- //
  
  private void setUpMessage() {
    messageValueObject = new MessageValueObject()
          .withContent(messageContent)
          .withId(messageId)
          .withAttachment(messageAttachment)
          .withAuthor(messageAuthor)
          .withAuthorId(messageAuthorId);
  }
  
  private void setUpUser() {
    userValueObject = new UserValueObject()
          .withId(userId)
          .withName(userName)
          .withDiscriminator(userDiscriminator)
          .withRanks(Lists.newArrayList(Rank.USER.name()));
  }
  
  private void setUpSelfUser() {
    selfUserValueObject = new UserValueObject()
          .withId(selfUserId)
          .withName(selfUserName)
          .withDiscriminator(selfUserDiscriminator)
          .withRanks(Lists.newArrayList(Rank.USER.name()));
  }
  
  private void setUpWebHook() {
    webHookValueObject = new WebHookValueObject()
          .withId(webHookId)
          .withName(webHookName)
          .withChannelId(webHookChannelId)
          .withOwner(webHookOwner)
          .withSubreddits(Lists.newArrayList(webHookSubreddit));
  }
  
  private void setUpRole() {
    roleValueObject = new RoleValueObject()
          .withGroup(roleGroup)
          .withId(roleId)
          .withName(roleName);
  }
  
  private void setUpTextChannel() {
    channelValueObject = new TextChannelValueObject()
          .withId(channelId)
          .withName(channelName)
          .withSubreddits(Lists.newArrayList(channelSubreddit));
  }
  
  private void setUpGuild() {
    guildValueObject = new GuildValueObject()
          .withId(guildId)
          .withName(guildName)
          .withPrefix(guildPrefix);
  }
  
  // -------------------------------------------------------------------------------------------- //
  
  private void setUpMessageView() {
    setUpMessage();
    
    messageView = mock(GuildMessage.class);
    when(messageView.getAbout()).thenReturn(messageValueObject);
  }
  
  private void setUpMemberView() {
    member = mock(Member.class);
    when(member.getPermissions()).thenReturn(Collections.emptySet());
    when(member.getAbout()).thenReturn(userValueObject);
  }
  
  private void setUpSelfUserView() {
    selfUser = mock(SelfUser.class);
    when(selfUser.getAbout()).thenReturn(selfUserValueObject);
  }
  
  private void setUpSelfMemberView() {
    selfMemberView = mock(SelfMember.class);
    when(selfMemberView.getPermissions()).thenReturn(Collections.emptySet());
    when(selfMemberView.getAbout()).thenReturn(selfUserValueObject);
  }
  
  private void setUpWebHookView() {
    webHook = mock(WebHook.class);
    when(webHook.getAbout()).thenReturn(webHookValueObject);
  }
  
  public void setUpRoleView() {
    role = mock(Role.class);
    when(role.getAbout()).thenReturn(roleValueObject);
  }
  
  private void setUpTextChannelView() {
    channelView = mock(TextChannel.class);
    when(channelView.getMessage(argThat(IdMatcher.of(messageId)))).thenReturn(messageView);
    when(channelView.getWebHook(any())).thenReturn(webHook);
    when(channelView.getWebHook(any(), anyBoolean())).thenReturn(webHook);
    when(channelView.getAbout()).thenReturn(channelValueObject);
  }
  
  private void setUpGuildView() {
    guild = mock(Guild.class);
    when(guild.getSelfMember()).thenReturn(selfMemberView);
    when(guild.getAbout()).thenReturn(guildValueObject);
    when(guild.getMember(argThat(IdMatcher.of(userId)))).thenReturn(member);
    when(guild.getMember(argThat(IdMatcher.of(selfUserId)))).thenReturn(selfMemberView);
    when(guild.getRole(argThat(IdMatcher.of(roleId)))).thenReturn(role);
    when(guild.getTextChannel(argThat(IdMatcher.of(channelId)))).thenReturn(channelView);
  }
  
  private void setUpShardView() {
    shard = mock(Shard.class);
    when(shard.getSelfUser()).thenReturn(selfUser);
    when(shard.getUser(argThat(IdMatcher.of(userId)))).thenReturn(member);
    when(shard.getUser(argThat(IdMatcher.of(selfUserId)))).thenReturn(selfMemberView);
    when(shard.getGuild(argThat(IdMatcher.of(guildId)))).thenReturn(guild);
  }
  
  /**
   * Initializes mocks for all Discord entities.
   */
  @BeforeEach
  public void setUpMocks() {
    setUpGuild();
    setUpRole();
    setUpSelfUser();
    setUpUser();
    setUpWebHook();
    setUpTextChannel();
  
    // Order is important! Some views depend on other views
    // TextChannelView -> GuildMessageView
    // ShardView -> SelfUserView, MemberView
    // GuildView -> SelfMemberView, MemberView, RoleView, TextChannelView
    setUpMessageView();
    setUpMemberView();
    setUpSelfUserView();
    setUpSelfMemberView();
    setUpWebHookView();
    setUpRoleView();
    setUpTextChannelView();
    setUpGuildView();
    setUpShardView();
  }
  
  // -------------------------------------------------------------------------------------------- //
  
  /**
   * Initializes all databases.
   *
   * @throws SQLException If a database error occurred.
   */
  @BeforeEach
  public void setUpDb() throws SQLException {
    GuildDatabase.create();
    RoleDatabase.create();
    TextChannelDatabase.create();
    WebHookDatabase.create();
    UserDatabase.create();
  }
  
  /**
   * Removes all databases on the file system.
   *
   * @throws IOException if one of the database files couldn't be deleted.
   */
  @AfterEach
  public void cleanUpDb() throws IOException {
    delete(GUILD_DB);
    delete(ROLE_DB);
    delete(CHANNEL_DB);
    delete(WEBHOOK_DB);
    delete(USER_DB);
  }
  
  private void delete(Path db) throws IOException {
    if (Files.exists(db)) {
      Files.delete(db);
    }
  }
  
  public Command parse(String content, Object... args) {
    return parse(String.format(content, args));
  }
  
  public Command parse(String content) {
    MessageValueObject message = new MessageValueObject();
    message.setContent(content);
    message.setAttachment(StringUtils.EMPTY);
    return parse(message);
  }
  
  private Command parse(MessageValueObject message) {
    GuildMessage source = mock(GuildMessage.class);
    
    when(source.getAbout()).thenReturn(message);
    when(source.getGuild()).thenReturn(guild);
    when(source.getAuthor()).thenReturn(member);
    when(source.getMessageChannel()).thenReturn(channelView);
    when(source.getShard()).thenReturn(shard);
    
    return parse(source);
  }
  
  private Command parse(GuildMessage source) {
    return parser.parse(source).orElseThrow();
  }
  
  private static class IdMatcher implements ArgumentMatcher<Argument> {
    private final long id;
    
    private IdMatcher(long id) {
      this.id = id;
    }
    
    public static IdMatcher of(long id) {
      return new IdMatcher(id);
    }
    
    @Override
    public boolean matches(Argument argument) {
      return Optional.ofNullable(argument)
            .flatMap(Argument::asNumber)
            .map(BigDecimal::longValue)
            .map(value -> value == id)
            .orElse(false);
    }
  }
}
