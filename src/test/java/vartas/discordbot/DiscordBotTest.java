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
package vartas.discordbot;

import com.google.common.collect.Sets;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA.Status;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import vartas.OfflineInstance;
import vartas.discordbot.messages.InteractiveMessage;
import vartas.xml.XMLDocumentException;
import vartas.xml.XMLServer;

/**
 *
 * @author u/Zavarov
 */
public class DiscordBotTest {
    static final String SERVER_CONTENT = 
"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
"<server>\n" +
"    <entry row=\"filter\" column=\"word\">\n" +
"        <document>\n" +
"            <entry>word</entry>\n" +
"        </document>\n" +
"    </entry>\n" +
"</server>";
    OfflineInstance instance;
    InteractiveMessage message;
    
    @BeforeClass
    public static void startUp() throws IOException{
        try (FileWriter writer = new FileWriter(new File("src/test/resources/guilds/0.server"))) {
            writer.write(SERVER_CONTENT);
        }
    }

    @Before
    public void setUp(){
        instance = new OfflineInstance();
        
        InteractiveMessage.Builder builder = new InteractiveMessage.Builder(instance.channel1, instance.user);
        builder.addLines(Arrays.asList("aaaaa","bbbbb"), 1);
        message = builder.build();
        message.send((c) -> {});
        message.accept(instance.guild_message);
        
        File file = new File("src/test/resources/guilds");
        if(file.exists()){
            for(File guild : file.listFiles()){
                if(!guild.getName().equals("0.server"))
                guild.delete();
            }
        }
        instance.messages.clear();
    }
    @After
    public void cleanUp(){
        File file = new File("src/test/resources/guilds");
        if(file.exists()){
            for(File guild : file.listFiles()){
                if(!guild.getName().equals("0.server")){
                    guild.delete();
                }
            }
        }
    }
    @Test
    public void getServerTest(){
        XMLServer server = instance.bot.getServer(instance.guild);
        assertEquals(server.getFilter(),Sets.newHashSet("word"));
    }
    @Test(expected=XMLDocumentException.class)
    public void getServerFailureTestTest() throws IOException{
        File file = new File("src/test/resources/guilds/100.server");
        file.createNewFile();
        file.setReadable(false);
        XMLServer server = instance.bot.getServer(new GuildImpl(instance.jda,100));
    }
    @Test
    public void deleteServerTest() throws IOException{
        File file = new File("src/test/resources/guilds/100000.server");
        file.createNewFile();
        
        assertTrue(file.exists());
        instance.bot.deleteServer(new GuildImpl(instance.jda,100000));
        assertFalse(file.exists());
    }
    @Test
    public void deleteUnknownServerTest(){
        File file = new File("src/test/resources/guilds/1.server");
        assertFalse(file.exists());
        instance.bot.deleteServer(new GuildImpl(instance.jda,1));
        assertFalse(file.exists());
    }
    @Test
    public void updateServerTest() throws IOException{
        File file = new File("src/test/resources/guilds/1000.server");
        file.createNewFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(SERVER_CONTENT);
        }
        
        instance.bot = new DiscordBot(null,instance.jda,instance.config, null, null, null);
        
        XMLServer server = instance.bot.getServer(new GuildImpl(instance.jda,1000));
        assertEquals(server.getFilter(),Sets.newHashSet("word"));
        
        server.addFilter("expression");
        instance.bot.updateServer(new GuildImpl(instance.jda,1000));
        
        server = instance.bot.getServer(new GuildImpl(instance.jda,1000));
        assertEquals(server.getFilter(),Sets.newHashSet("word","expression"));
        new File("src/test/resources/guilds/1000.server").delete();
    }
    @Test
    public void updateServerFailureTest() throws IOException{
        instance.bot = new DiscordBot(null,instance.jda,instance.config, null, null, null);
        instance.bot.getServer(new GuildImpl(instance.jda,100));
        File file = new File("src/test/resources/guilds/100.server");
        file.createNewFile();
        file.setWritable(false);
        instance.bot.updateServer(new GuildImpl(instance.jda,100));
        file.setWritable(true);
    }
    @Test
    public void updateServerUnknownGuildTest() throws IOException{
        instance.bot = new DiscordBot(null,instance.jda,instance.config, null, null, null);
        File file = new File("src/test/resources/guilds/10000.server");
        instance.bot.updateServer(new GuildImpl(instance.jda,10000));
        assertFalse(file.exists());
    }
    @Test
    public void getRuntimeTest(){
        assertEquals(instance.bot.getRuntime(),instance.bot.runtime);
    }
    @Test
    public void getJDATest(){
        assertEquals(instance.bot.getJda(),instance.jda);
    }
    @Test
    public void shutdownTest(){
        instance.jda.setStatus(Status.CONNECTED);
        assertEquals(instance.bot.getJda().getStatus(),Status.CONNECTED);
        
        instance.bot.shutdown();
        
        assertEquals(instance.bot.getJda().getStatus(),Status.DISCONNECTED);
    }
    @Test
    public void sendMessageStringTest(){
        DiscordBot.sendMessage(instance.channel1, "stuff");
        assertEquals(instance.messages.get(0).getContentRaw(),"stuff");
    }
    @Test
    public void sendMessageEmbedTest(){
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Author");
        DiscordBot.sendMessage(instance.channel1, builder.build());
        assertEquals(instance.messages.get(0).getEmbeds().get(0).getAuthor().getName(),"Author");
    }
    @Test
    public void sendMessageBuilderTest(){
        MessageBuilder builder = new MessageBuilder();
        builder.append("stuff");
        DiscordBot.sendMessage(instance.channel1, builder);
        assertEquals(instance.messages.get(0).getContentRaw(),"stuff");
    }
    @Test
    public void sendFileTest() throws IOException{
        instance.actions.clear();
        File file = new File("src/test/resources/config.xml");
        DiscordBot.sendFile(instance.channel1, file);
        assertEquals(instance.actions,Arrays.asList("config.xml"));
    }
    @Test
    public void sendImageTest() throws IOException{
        instance.actions.clear();
        BufferedImage image = ImageIO.read(new File("src/test/resources/image.png"));
        DiscordBot.sendImage(image,instance.channel1);
        assertEquals(instance.actions,Arrays.asList("plot.png"));
    }
}