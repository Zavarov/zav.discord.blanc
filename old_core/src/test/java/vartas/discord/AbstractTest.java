package vartas.discord;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.internal.utils.config.AuthorizationConfig;

public abstract class AbstractTest {
    protected static AuthorizationConfig Authorization = new AuthorizationConfig(AccountType.BOT, "12345");
    protected static long guildId = 0L;
    protected static long roleId = 2L;
    protected static long channelId = 1L;
    protected static long userId = 3L;
}
