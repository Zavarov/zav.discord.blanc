/*
 * Copyright (C) 2018 u/Zavarov
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
package vartas.discordbot.comm;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.entities.impl.MemberImpl;
import net.dv8tion.jda.core.entities.impl.RoleImpl;
import net.dv8tion.jda.core.entities.impl.SelfUserImpl;
import net.dv8tion.jda.core.entities.impl.TextChannelImpl;
import net.dv8tion.jda.core.entities.impl.UserImpl;
import net.dv8tion.jda.core.requests.RestAction;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import vartas.xml.XMLServer;

/**
 *
 * @author u/Zavarov
 */
public class CommunicatorTest {
    static OfflineEnvironment environment;
    static OfflineCommunicator comm;
    static GuildImpl guild0;
    static UserImpl user0;
    static MemberImpl member0;
    static MemberImpl memberself;
    static RoleImpl role0;
    static SelfUserImpl self;
    static TextChannelImpl channel0;
    @BeforeClass
    public static void setUp(){
        environment = new OfflineEnvironment();
        comm = (OfflineCommunicator)environment.shards.get(0);
        
        comm.activity.shutdown();
        comm.messages.shutdown();
        
        user0 = new UserImpl(0L,(JDAImpl)comm.jda);
        self = new SelfUserImpl(2L,(JDAImpl)comm.jda);
        
        ((JDAImpl)comm.jda).getUserMap().put(self.getIdLong(),self);
        ((JDAImpl)comm.jda).getUserMap().put(user0.getIdLong(),user0);
        
        guild0 = new GuildImpl((JDAImpl)comm.jda,0L);
        
        guild0.setName("guild0");
        
        ((JDAImpl)comm.jda).getGuildMap().put(guild0.getIdLong(),guild0);
        
        //Custom action for when the interactive message is sent.
        channel0 = new TextChannelImpl(0L,guild0);
        
        channel0.setName("channel0");
        
        guild0.getTextChannelsMap().put(channel0.getIdLong(),channel0);
        
        role0 = new RoleImpl(0L,guild0);
        
        role0.setName("role0");
        
        guild0.getRolesMap().put(role0.getIdLong(),role0);
        
        memberself = new MemberImpl(guild0,self);
        member0 = new MemberImpl(guild0,user0);
        
        guild0.getMembersMap().put(user0.getIdLong(),member0);
        guild0.getMembersMap().put(self.getIdLong(),memberself);
        
        
        ((JDAImpl)comm.jda).setSelfUser(self);
        guild0.setPublicRole(role0);
        guild0.setOwner(member0);
        role0.setRawPermissions(Permission.ALL_PERMISSIONS);
    }
    @Before
    public void cleanUp(){
        comm.actions.clear();
        comm.discord.clear();
    }
    @Test
    public void sendStringTest(){
        comm.send(channel0, "content");
        assertEquals(comm.discord.get(channel0).get(0).getContentRaw(),"content");
    }
    @Test
    public void sendEmbedTest(){
        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription("description");
        MessageEmbed embed = builder.build();
        comm.send(channel0, embed);
        assertEquals(comm.discord.get(channel0).get(0).getEmbeds(), Arrays.asList(embed));
    }
    @Test
    public void sendImageTest() throws IOException{
        BufferedImage image = ImageIO.read(new File("src/test/resources/image.png"));
        comm.send(channel0, image);
        assertEquals(comm.actions,Arrays.asList("action queued"));
    }
    @Test(expected=IllegalArgumentException.class)
    public void sendImageIoExceptionTest() throws IOException{
        BufferedImage image = ImageIO.read(new File("src/test/resources/image.png"));
        Communicator _comm = new OfflineCommunicator(environment,comm.jda()){
            @Override
            public <T> void send(RestAction<T> action){
                throw new RuntimeException();
            }
        };
        
        _comm.send(channel0, image);
        
    }
    @Test(expected=IllegalArgumentException.class)
    public void sendInvalidImageTest(){
        BufferedImage image = null;
        comm.send(channel0, image);
    }
    @Test
    public void sendFileTest() throws IOException{
        comm.send(channel0, new File("src/test/resources/image.png"));
        assertEquals(comm.actions,Arrays.asList("action queued"));
    }
    @Test
    public void serverRoleTest(){
        XMLServer server = comm.server(role0);
        assertEquals(server.getPrefix(),"prefix");
    }
    @Test
    public void serverChannelTest(){
        XMLServer server = comm.server(channel0);
        assertEquals(server.getPrefix(),"prefix");
    }
}
