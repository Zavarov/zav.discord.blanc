/*
 * Copyright (C) 2016 u/Zavarov
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
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.utils.JDALogger;
import org.slf4j.Logger;
import vartas.discordbot.threads.RedditFeed;
import vartas.discordbot.threads.StatusTracker;
import vartas.reddit.PushshiftWrapper;
import vartas.reddit.RedditBot;
import vartas.xml.XMLConfig;
import vartas.xml.XMLCredentials;
import vartas.xml.XMLDocument;
import vartas.xml.XMLServer;
/**
 * This class implements all the features for a bot on a single shard.
 * @author u/Zavarov
 */
public class DiscordBot{
    /**
     * The log for all events in this bot.
     */
    protected final Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    /**
     * The main instance of the runtime.
     */
    protected final DiscordRuntime runtime;
    /**
     * The JDA for this specific bot.
     */
    protected final JDA jda;
    /**
     * The XML file will be accessed quite frequently. 
     */
    protected final Map<Guild, XMLServer> guilds = new Object2ObjectOpenHashMap<>();
    /**
     * The configuration file.
     */
    protected final XMLConfig config;
    /**
     * The listener that receives all the messages from Discord.
     */
    protected final DiscordMessageListener listener;
    /**
     * The tracker for all status messages.
     */
    protected final StatusTracker status;
    /**
     * Creates a new instance of the bot with a custom listener.
     * @param runtime the main instance of this runtime.
     * @param jda the JDA for this specific bot.
     * @param config the configuration file.
     * @param reddit the instance that communicates with the Reddit API.
     * @param wrapper the instance of the crawler that contains all previously stored comments.
     * @param feed the runnable that checks for new submissions.
     */
    public DiscordBot(DiscordRuntime runtime, JDA jda, XMLConfig config, RedditBot reddit, PushshiftWrapper wrapper, RedditFeed feed){
        this.runtime = runtime;
        this.jda = jda;
        this.config = config;
        XMLCredentials credentials = XMLCredentials.create(new File(String.format("%s/credentials.xml",config.getDataFolder())));
        this.listener = new DiscordMessageListener(DiscordBot.this, config, reddit, wrapper, feed);
        this.status = new StatusTracker(jda, new File(String.format("%s/status.xml",config.getDataFolder())),config.getStatusInterval());
        
        jda.addEventListener(listener);
        
        log.info(String.format("Initialized the bot #%s",jda.getShardInfo().getShardId()));
    }
    
    /**
     * @param guild the guild whose file we want.
     * @return the configuration file for the guild.
     */
    public XMLServer getServer(Guild guild){
        if(guilds.containsKey(guild)){
            return guilds.get(guild);
        }else{
            File file = new File(String.format("%s/guilds/%s.server",config.getDataFolder(),guild.getId()));
            XMLServer server = file.exists() ? XMLServer.create(file) : new XMLServer();
            guilds.put(guild, server);
            return server;
        }
    }
    /**
     * Deletes the configuration file of the guild.
     * @param guild the guild whose configuration file is deleted.
     */
    public void deleteServer(Guild guild){
        File file = new File(String.format("%s/guilds/%s.server",config.getDataFolder(),guild.getId()));
        if(file.exists()){
            file.delete();
            log.info(String.format("Deleted server %s",guild));
        }
    }
    /**
     * Overwrites the former configuration file of the guild, if such a thing exists.
     * @param guild the guild the configuration file of is updated.
     */
    public void updateServer(Guild guild){
        try{
            if(guilds.containsKey(guild)){
                File file = new File(String.format("%s/guilds/%s.server",config.getDataFolder(),guild.getId()));
                XMLServer server = guilds.get(guild);
                XMLDocument document = server.update();
                document.write(new FileOutputStream(file), null);
                log.info(String.format("Updated the file for the server %s", guild.getId()));
            }
        }catch(InterruptedException | IOException e){
            log.error(e.toString());
        }
    }
    /**
     * @return the runtime of this program.
     */
    public DiscordRuntime getRuntime(){
        return runtime;
    }
    /**
     * @return the JDA instance of this bot.
     */
    public JDA getJda(){
        return jda;
    }
    /**
     * Terminates all threads that are a part of this bot.
     */
    public void shutdown(){
        jda.shutdown();
        listener.shutdown();
        status.shutdown();
        log.info(String.format("Bot %s terminated.",jda.getShardInfo().getShardString()));
    }
    /**
     * Sends a message containing only a string in a channel.
     * @param channel a textchannel.
     * @param content the raw content of the message.
     */
    public static void sendMessage(MessageChannel channel, String content){
        MessageBuilder builder = new MessageBuilder();
        builder.append(content);
        builder.stripMentions(channel.getJDA());
        sendMessage(channel,builder);
    }
    /**
     * Sends a message containing only an embed segment in a channel.
     * @param channel the textchannel.
     * @param content the embed message.
     */
    public static void sendMessage(MessageChannel channel, MessageEmbed content){
        MessageBuilder builder = new MessageBuilder();
        builder.setEmbed(content);
        sendMessage(channel,builder);
    }
    /**
     * Sends a message in a channel.
     * @param channel the textchannel.
     * @param content the message.
     */
    public static void sendMessage(MessageChannel channel, MessageBuilder content){
        sendMessage(channel,content,null);
    }
    /**
     * Sends a message in a channel and executes a function when the action was successful.
     * @param channel the textchannel.
     * @param content the message.
     * @param success a function when the message was send successfully.
     */
    public static void sendMessage(MessageChannel channel, MessageBuilder content, Consumer<Message> success){
        sendMessage(channel,content,success, null);
    }
    /**
     * Sends a message in a channel and executes a function when the action was successful or failed.
     * @param channel the textchannel.
     * @param content the message.
     * @param success a function when the message was send successfully.
     * @param failure a function when the message wasn't send successfully.
     */
    public static void sendMessage(MessageChannel channel, MessageBuilder content, Consumer<Message> success, Consumer<Throwable> failure){
        content.stripMentions(channel.getJDA());
        channel.sendMessage(content.build()).queue(success, failure);
    }
    /**
     * Attaches a file to a message and uploads it in the channel.
     * @param channel the textchannel.
     * @param file the file that is supposed to be uploaded.
     * @throws IOException if the file is invalid.
     */
    public static void sendFile(MessageChannel channel, File file) throws IOException{
        sendAction(channel.sendFile(file));
    }
    /**
     * Executes an unspecified action.
     * @param <T> the return value of the action.
     * @param action the action.
     */
    public static <T> void sendAction(RestAction<T> action){
        sendAction(action, null);
    }
    /**
     * Executes an unspecified action.
     * @param <T> the return value of the action.
     * @param action the action.
     * @param success a function executed when the action as successful.
     */
    public static <T> void sendAction(RestAction<T> action, Consumer<T> success){
        sendAction(action,success,null);
    }
    /**
     * Executes an unspecified action.
     * @param <T> the return value of the action.
     * @param action the action.
     * @param success a function executed when the action as successful.
     * @param failure a function executed when the action failed.
     */
    public static <T> void sendAction(RestAction<T> action, Consumer<T> success, Consumer<Throwable> failure){
        action.queue(success, failure);
    }
    /**
     * Sends an image in the specified channel.
     * @param image the reference image.
     * @param channel the target channel.
     * @throws IOException in case the image couldn't be transformed into a bytestream.
     */
    public static void sendImage(BufferedImage image, MessageChannel channel) throws IOException{
        //Transform the image into a stream
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        InputStream is;
        ImageIO.write(image,"png", os);
        is = new ByteArrayInputStream(os.toByteArray());
        //Send the image
        sendAction(channel.sendFile(is,"plot.png"));
    }
}