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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.entities.impl.MemberImpl;
import net.dv8tion.jda.core.entities.impl.SelfUserImpl;
import net.dv8tion.jda.core.entities.impl.TextChannelImpl;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.requests.restaction.MessageAction;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import vartas.xml.XMLConfig;

/**
 *
 * @author u/Zavarov
 */
public class DefaultCommunicatorTest {
    static JDAImpl jda;
    static SelfUserImpl self;
    static MemberImpl memberself;
    static GuildImpl guild0;
    static GuildImpl guild1;
    static TextChannelImpl channel;
    static DefaultCommunicator comm;
    static List<String> actions = new ArrayList<>();
    static MessageBuilder message;
    @BeforeClass
    public static void startUp(){
        comm = new DefaultCommunicator(new OfflineEnvironment(){
            @Override
            public XMLConfig config(){
                return XMLConfig.create(new File("config.xml"));
            }
        }, OfflineEnvironment.create()){
            @Override
            public <T> void send(RestAction<T> action){
                actions.add("deleted");
            }
            @Override
            public void execute(Runnable runnable){
                runnable.run();
            }
        };
        
        jda = (JDAImpl)comm.jda();
        guild0 = new GuildImpl(jda,0L);
        guild1 = new GuildImpl(jda,1L);
        channel = new TextChannelImpl(0L,guild0){
            @Override
            public MessageAction sendMessage(Message message){
                return new MessageAction(jda,null,channel){
                    @Override
                    public void queue(Consumer<? super Message> success, Consumer<? super Throwable> failure){
                        actions.add("action");
                    }
                };
            }
        };
        self = new SelfUserImpl(0L,jda);
        memberself = new MemberImpl(guild0,self);
        
        jda.getGuildMap().put(guild0.getIdLong(), guild0);
        jda.getGuildMap().put(guild1.getIdLong(), guild1);
        guild0.getTextChannelsMap().put(channel.getIdLong(), channel);
        guild0.getMembersMap().put(self.getIdLong(),memberself);
        
        jda.setSelfUser(self);
        guild0.setOwner(memberself);
        
        message = new MessageBuilder().append("message");
    }
    @Before
    public void setUp(){
        actions.clear();
    }
    @Test
    public void sendRestActionTest(){
        comm.send(new RestAction.EmptyRestAction<Void>(jda, null){
            @Override
            public void queue(Consumer<? super Void> success, Consumer<? super Throwable> failure){
                actions.add("action");
            }
        }, null, null);
        assertEquals(actions,Arrays.asList("action"));
    }
    @Test
    public void sendMessageTest(){
        comm.send(channel,message,null,null);
        assertEquals(actions,Arrays.asList("action"));
    }
    @Test
    public void deleteMessageTest(){
        comm.delete(channel, 0);
        assertEquals(actions,Arrays.asList("deleted"));
    }
    @Test
    public void updateGuildTest() throws IOException, InterruptedException{
        //Load the server file
        comm.server(guild0);
        File file = new File("data/guilds/0.server");
        file.delete();
        assertFalse(file.exists());
        comm.update(guild0);
        assertTrue(file.exists());
    }
    @Test
    public void updatePermissionTest() throws IOException, InterruptedException{
        File file = new File("data/permission.xml");
        file.delete();
        assertFalse(file.exists());
        comm.update(comm.environment.permission());
        assertTrue(file.exists());
    }
    @Test
    public void deleteGuildTest() throws IOException, InterruptedException{
        File file = new File("data/guilds/1.server");
        comm.server(guild1).update().write(new FileOutputStream(file), null);
        assertTrue(comm.servers.containsKey(guild1));
        assertTrue(file.exists());
        comm.delete(guild1);
        assertFalse(file.exists());
        assertFalse(comm.servers.containsKey(guild1));
    }
    @Test
    public void deleteGuildNoFileTest(){
        File file = new File("data/guilds/1.server");
        assertFalse(file.exists());
        comm.delete(guild1);
        assertFalse(file.exists());
    }
}
