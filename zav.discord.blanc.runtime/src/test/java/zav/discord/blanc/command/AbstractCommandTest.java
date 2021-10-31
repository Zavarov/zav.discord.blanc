package zav.discord.blanc.command;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentMatcher;
import zav.discord.blanc.Argument;
import zav.discord.blanc.Rank;
import zav.discord.blanc.command.parser.Parser;
import zav.discord.blanc.databind.*;
import zav.discord.blanc.db.*;
import zav.discord.blanc.mc.MontiCoreCommandParser;
import zav.discord.blanc.runtime.internal.CommandResolver;
import zav.discord.blanc.view.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public abstract class AbstractCommandTest {
  protected User user;
  protected User selfUser;
  protected Role role;
  protected Guild guild;
  protected TextChannel channel;
  protected Message message;
  protected WebHook webHook;
  
  protected SelfUserView selfUserView;
  protected GuildView guildView;
  protected MemberView memberView;
  protected SelfMemberView selfMemberView;
  protected TextChannelView channelView;
  protected ShardView shardView;
  protected RoleView roleView;
  protected GuildMessageView messageView;
  protected WebHookView webHookView;
  
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
  
  private static Parser parser;
  private static final Path GUILD_DB = Paths.get("Guild.db");
  private static final Path ROLE_DB = Paths.get("Role.db");
  private static final Path CHANNEL_DB = Paths.get("TextChannel.db");
  private static final Path WEBHOOK_DB = Paths.get("WebHook.db");
  private static final Path USER_DB = Paths.get("User.db");
  
  @BeforeAll
  public static void setUpAll() {
    parser = new MontiCoreCommandParser();
    
    CommandResolver.init();
  }
  
  // -------------------------------------------------------------------------------------------- //
  
  private void setUpMessage() {
    message = new Message();
    message.setContent(messageContent);
    message.setId(messageId);
    message.setAttachment(messageAttachment);
    message.setAuthor(messageAuthor);
    message.setAuthorId(messageAuthorId);
  }
  
  private void setUpUser() {
    user = new User();
    user.setId(userId);
    user.setName(userName);
    user.setDiscriminator(userDiscriminator);
    user.getRanks().add(Rank.USER.name());
  }
  
  private void setUpSelfUser() {
    selfUser = new User();
    selfUser.setId(selfUserId);
    selfUser.setName(selfUserName);
    selfUser.setDiscriminator(selfUserDiscriminator);
    selfUser.getRanks().add(Rank.USER.name());
  }
  
  private void setUpWebHook() {
    webHook = new WebHook();
    webHook.setId(webHookId);
    webHook.setName(webHookName);
    webHook.setChannelId(webHookChannelId);
    webHook.setOwner(webHookOwner);
    webHook.getSubreddits().add(webHookSubreddit);
  }
  
  private void setUpRole() {
    role = new Role();
    role.setGroup(roleGroup);
    role.setId(roleId);
    role.setName(roleName);
  }
  
  private void setUpTextChannel() {
    channel = new TextChannel();
    channel.setId(channelId);
    channel.setName(channelName);
    channel.getSubreddits().add(channelSubreddit);
  }
  
  private void setUpGuild() {
    guild = new Guild();
    guild.setId(guildId);
    guild.setName(guildName);
    guild.setPrefix(guildPrefix);
  }
  
  // -------------------------------------------------------------------------------------------- //
  
  private void setUpMessageView() {
    setUpMessage();
    
    messageView = mock(GuildMessageView.class);
    when(messageView.getAbout()).thenReturn(message);
  }
  
  private void setUpMemberView() {
    memberView = mock(MemberView.class);
    when(memberView.getPermissions()).thenReturn(Collections.emptySet());
    when(memberView.getAbout()).thenReturn(user);
  }
  
  private void setUpSelfUserView() {
    selfUserView = mock(SelfUserView.class);
    when(selfUserView.getAbout()).thenReturn(selfUser);
  }
  
  private void setUpSelfMemberView() {
    selfMemberView = mock(SelfMemberView.class);
    when(selfMemberView.getPermissions()).thenReturn(Collections.emptySet());
    when(selfMemberView.getAbout()).thenReturn(selfUser);
  }
  
  private void setUpWebHookView() {
    webHookView = mock(WebHookView.class);
    when(webHookView.getAbout()).thenReturn(webHook);
  }
  
  public void setUpRoleView() {
    roleView = mock(RoleView.class);
    when(roleView.getAbout()).thenReturn(role);
  }
  
  private void setUpTextChannelView() {
    channelView = mock(TextChannelView.class);
    when(channelView.getMessage(argThat(IdMatcher.of(messageId)))).thenReturn(messageView);
    when(channelView.getWebhook(any())).thenReturn(webHookView);
    when(channelView.getWebhook(any(), anyBoolean())).thenReturn(webHookView);
    when(channelView.getAbout()).thenReturn(channel);
  }
  
  private void setUpGuildView() {
    guildView = mock(GuildView.class);
    when(guildView.getSelfMember()).thenReturn(selfMemberView);
    when(guildView.getAbout()).thenReturn(guild);
    when(guildView.getMember(argThat(IdMatcher.of(userId)))).thenReturn(memberView);
    when(guildView.getMember(argThat(IdMatcher.of(selfUserId)))).thenReturn(selfMemberView);
    when(guildView.getRole(argThat(IdMatcher.of(roleId)))).thenReturn(roleView);
    when(guildView.getTextChannel(argThat(IdMatcher.of(channelId)))).thenReturn(channelView);
  }
  
  private void setUpShardView() {
    shardView = mock(ShardView.class);
    when(shardView.getSelfUser()).thenReturn(selfUserView);
    when(shardView.getUser(argThat(IdMatcher.of(userId)))).thenReturn(memberView);
    when(shardView.getUser(argThat(IdMatcher.of(selfUserId)))).thenReturn(selfMemberView);
    when(shardView.getGuild(argThat(IdMatcher.of(guildId)))).thenReturn(guildView);
  }
  
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
  
  @BeforeEach
  public void setUpDb() throws SQLException {
    GuildTable.create();
    RoleTable.create();
    TextChannelTable.create();
    WebHookTable.create();
    UserTable.create();
  }
  
  @AfterEach
  public void cleanUpDb() throws IOException {
    delete(GUILD_DB);
    delete(ROLE_DB);
    delete(CHANNEL_DB);
    delete(WEBHOOK_DB);
    delete(USER_DB);
  }
  
  private void delete(Path DB) throws IOException {
    if(Files.exists(DB)) {
      Files.delete(DB);
    }
  }
  
  public Command parse(String content, Object... args) {
    return parse(String.format(content, args));
  }
  
  public Command parse(String content) {
    Message message = new Message();
    message.setContent(content);
    message.setAttachment(StringUtils.EMPTY);
    return parse(message);
  }
  
  private Command parse(Message message) {
    GuildMessageView source = mock(GuildMessageView.class);
    
    when(source.getAbout()).thenReturn(message);
    when(source.getGuild()).thenReturn(guildView);
    when(source.getAuthor()).thenReturn(memberView);
    when(source.getMessageChannel()).thenReturn(channelView);
    when(source.getShard()).thenReturn(shardView);
    
    return parse(source);
  }
  
  private Command parse(GuildMessageView source) {
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
