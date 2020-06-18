package vartas.discord.blanc.mock;

import vartas.discord.blanc.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DiscordServerHookPointMock implements ServerHookPoint {
    public List<Message> sent = new ArrayList<>();

    @Override
    public void send(@Nonnull MessageChannel messageChannel, @Nonnull Message message) {
        sent.add(message);
    }

    @Override
    public void send(MessageChannel messageChannel, byte[] bytes, String qualifiedName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public User getSelfUser() {
        throw new UnsupportedOperationException();
    }
}
