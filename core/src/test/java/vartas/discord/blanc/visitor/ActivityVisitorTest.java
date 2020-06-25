package vartas.discord.blanc.visitor;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vartas.discord.blanc.*;
import vartas.discord.blanc.factory.GuildFactory;
import vartas.discord.blanc.factory.MessageFactory;
import vartas.discord.blanc.factory.TextChannelFactory;
import vartas.discord.blanc.factory.UserFactory;
import vartas.discord.blanc.mock.GuildMock;
import vartas.discord.blanc.mock.SelfMemberMock;
import vartas.discord.blanc.mock.TextChannelMock;
import vartas.discord.blanc.mock.UserMock;

import java.time.Instant;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class ActivityVisitorTest extends AbstractTest {
    User author;
    Message message;
    TextChannel textChannel;
    Guild guild;
    @BeforeEach
    public void setUp(){
        author = UserFactory.create(UserMock::new, 0, "user");
        message = MessageFactory.create(0, Instant.now(), author);
        textChannel = TextChannelFactory.create(TextChannelMock::new, 0, "TextChannel");
        guild =  GuildFactory.create(GuildMock::new, new SelfMemberMock(), 0, "Guild");

        textChannel.putMessages(message.getId(), message);
    }

    @Test
    public void testAccept(){
        JFreeChart chart = ActivityVisitor.create(guild, Collections.singletonList(textChannel));
        XYPlot plot = chart.getXYPlot();

        assertThat(plot.getDatasetCount()).isEqualTo(1);
    }
}
