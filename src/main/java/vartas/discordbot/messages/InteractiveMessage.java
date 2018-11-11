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
package vartas.discordbot.messages;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.requests.restaction.MessageAction;
import net.dv8tion.jda.core.utils.PermissionUtil;
import vartas.discordbot.comm.Communicator;

/**
 * This class creates an interactive message where the user has the option to
 * move between different "pages".
 * @author u/Zavarov
 */
public class InteractiveMessage implements Consumer<Message>{
    /**
     * &lt;-.
     */
    public static final String ARROW_LEFT = "\u2b05";

    /**
     * -&gt;.
     */
    public static final String ARROW_RIGHT = "\u27a1";
    /**
     * All possible pages.
     */
    protected final List<MessageEmbed> pages;
    /**
     * The user that caused this message to be sent.
     */
    protected final User author;
    /**
     * The channel the message has to be sent in.
     */
    protected final MessageChannel channel;
    /**
     * The timestamp of the last time the author interacted with this message.
     */
    protected OffsetDateTime last_reaction;
    /**
     * The page that is displayed.
     */
    protected int current_page = 0;
    /**
     * The message containing the current page.
     */
    protected Message current_message;
    /**
     * The consumer that deals with interactive messages.
     */
    protected Consumer<InteractiveMessage> consumer;
    /**
     * The communicator for the current shard.
     */
    protected Communicator comm;
    /**
     * creates an message with the specified pages.
     * @param channel the channel the message is in.
     * @param author the author who can interact with the message.
     * @param pages the content of the message.
     * @param comm the communicator for the shard the message is in.
     */
    protected InteractiveMessage(MessageChannel channel, User author, List<MessageEmbed> pages, Communicator comm){
        this.channel = channel;
        this.comm = comm;
        this.author = author;
        this.pages = pages;
        this.last_reaction = OffsetDateTime.now();
    }
    /**
     * Submits this message to the Discord API.
     * @param consumer the consumer that will add this message to the thread that manages interactive messages.
     * @return the rest action that will send this message.
     */
    public RestAction<Message> toRestAction(Consumer<InteractiveMessage> consumer){
        this.consumer = consumer;
        return channel.sendMessage(pages.get(current_page));
    }
    
    /**
     * Registers a reaction from a specific user.
     * @param user the user.
     * @param reaction the reaction.
     */
    public void add(User user, MessageReaction reaction){
        if(author.equals(user)){
            last_reaction = OffsetDateTime.now();
            
            if(reaction.getReactionEmote().getName().equals(ARROW_LEFT))
                current_page = (current_page-1+pages.size()) % pages.size();
            else if(reaction.getReactionEmote().getName().equals(ARROW_RIGHT))
                current_page = (current_page+1) % pages.size();
            
            MessageEmbed next_message = pages.get(current_page);
            MessageAction update = current_message.editMessage(next_message);
            Consumer<Message> update_message = m -> current_message = m;
            
            comm.send(update,update_message);
            //Remove the reaction so that the user doesn't have to do it
            if(isTextChannel() && canRemoveReactions()){
                comm.send(reaction.removeReaction(user));
            }
        }
    }
    /**
     * @return the most recent message. 
     */
    public Message getCurrentMessage(){
        return current_message;
    }
    /**
     * @return the timestamp of the last interaction.
     */
    public OffsetDateTime getLastReaction() {
        return last_reaction;
    }
    /**
     * @return true when the message is in a text channel. 
     */
    private boolean isTextChannel(){
        return current_message.getChannelType() == ChannelType.TEXT;
    }
    /**
     * @return true when the bot has the "Manage Messages" permission. 
     */
    private boolean canRemoveReactions(){
        long raw_permission = PermissionUtil.getEffectivePermission(current_message.getTextChannel(), 
                current_message.getGuild().getSelfMember()
        );
        List<Permission> permission = Permission.getPermissions(raw_permission);
        return permission.contains(Permission.MESSAGE_MANAGE);
    }
    /**
     * Adds the reactions to the message if it was send successfully.
     * @param message the message that was send.
     */
    @Override
    public void accept(Message message){
        current_message = message;
        comm.send(message.addReaction(ARROW_LEFT));
        comm.send(message.addReaction(ARROW_RIGHT));
        consumer.accept(this);
    }
    /**
     * The builder for creating this kind of messages.
     */
    public static class Builder{
        /**
         * The current pages.
         */
        protected final List<EmbedBuilder> embeds = new ObjectArrayList<>();
        /**
         * The channel the message is sent to.
         */
        protected final MessageChannel channel;
        /**
         * THe user who can interact with the message.
         */
        protected final User author;
        /**
         * The builder for the current message.
         */
        protected final StringBuilder current_page = new StringBuilder();
        /**
         * The title of each page.
         */
        protected String description;
        /**
         * The thumbnail for all pages.
         */
        protected String thumbnail;
        /**
         * The communicator of the shard the message is in.
         */
        protected Communicator comm;
        /**
         * Initializes an empty builder.
         * @param channel the channel the message is sent in.
         * @param author the user who can interact with the message.
         * @param comm the communicator of the shard the message is in.
         */
        public Builder(MessageChannel channel, User author, Communicator comm){
            this.channel = channel;
            this.author = author;
            this.comm = comm;
        }
        /**
         * Adds a description for this and all following pages.
         * In order to stop that, call this function with null.
         * @param description the new description.
         * @return this.
         */
        public Builder addDescription(String description){
            this.description = description;
            return this;
        }
        /**
         * Adds a single line to the message, followed by a line break.
         * @param line the line that is added to the current page.
         * @return this.
         */
        public Builder addLine(String line){
            current_page.append(line).append("\n");
            return this;
        }
        /**
         * Adds all lines over multiple pages.
         * @param lines all lines that are added.
         * @param elements_per_page the elements each page should have.
         * @return this.
         */
        public Builder addLines(Collection<?> lines, int elements_per_page){
            Iterator<?> iterator = lines.iterator();
            for(int j = 0 ; j < lines.size() ; j+=elements_per_page){
                for(int i = j ; i < Math.min(j+elements_per_page,lines.size()) ; ++i){
                    addLine(iterator.next().toString());
                }
                //Don't do it for the last page
                if( (j+elements_per_page) < lines.size()){
                    nextPage();
                }
            }
            return this;
        }
        /**
         * Saves the content of the current page and creates a new one.
         * @return this.
         */
        public Builder nextPage(){
            embeds.add(createPage());
            current_page.setLength(0);
            return this;
        }
        /**
         * Adds a new page to the message.
         * @param embed the complete page.
         * @return this.
         */
        public Builder addPage(EmbedBuilder embed){
            embeds.add(embed);
            return this;
        }
        /**
         * Sets the thumbnail for every page.
         * @param thumbnail the URL of the thumnbnail.
         * @return this.
         */
        public Builder setThumbnail(String thumbnail){
            this.thumbnail = thumbnail;
            return this;
        }
        /**
         * Sets the title for all pages after the size is known.
         * @param embeds the pages.
         * @return this.
         */
        private Builder updateTitle(List<EmbedBuilder> embeds){
            String title;
            for(int i = 0 ; i < embeds.size() ; ++i){
                title = String.format("Page %d/%d",i,embeds.size()-1);
                embeds.set(i, embeds.get(i).setTitle(title));
            }
            return this;
        }
        /**
         * Updates the thumbnail for every page.
         * @param embeds the pages.
         * @return this.
         */
        private Builder updateThumbnail(List<EmbedBuilder> embeds){
            if(thumbnail != null){
                embeds.forEach(builder -> builder.setThumbnail(thumbnail));
            }
            return this;
        }
        /**
         * @return the page with description and content. 
         */
        private EmbedBuilder createPage(){
            EmbedBuilder embed = new EmbedBuilder();
            if(description != null){
                embed.addField("Description",description,false);
            }
            embed.addField("",current_page.toString(),false);
            return embed;
        }
        /**
         * @return the final message. 
         */
        public InteractiveMessage build(){
            List<EmbedBuilder> output = new ObjectArrayList<>(embeds.size()+1);
            output.addAll(embeds);
            //Add the current page as well
            if(current_page.length() > 0){
                output.add(createPage());
            }
            updateTitle(output);
            updateThumbnail(output);
            List<MessageEmbed> pages = output.stream()
                    .map(EmbedBuilder::build)
                    .collect(Collectors.toList());
            return new InteractiveMessage(channel,author,pages,comm);
        }
    }
}