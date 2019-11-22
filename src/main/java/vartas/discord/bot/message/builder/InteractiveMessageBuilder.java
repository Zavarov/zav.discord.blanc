/*
 * Copyright (c) 2019 Zavarov
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

package vartas.discord.bot.message.builder;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import vartas.discord.bot.entities.DiscordCommunicator;
import vartas.discord.bot.message.InteractiveMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class InteractiveMessageBuilder {
    /**
     * The current pages.
     */
    protected final List<EmbedBuilder> embeds = new ArrayList<>();
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
    protected DiscordCommunicator comm;
    /**
     * All fields in the current page.
     */
    protected List<MessageEmbed.Field> fields = new ArrayList<>();
    /**
     * Initializes an empty builder.
     * @param author the user who can interact with the message.
     * @param comm the communicator of the shard the message is in.
     */
    public InteractiveMessageBuilder(User author, DiscordCommunicator comm){
        this.author = author;
        this.comm = comm;
    }
    /**
     * Adds a description for this and all following pages.
     * In order to stop that, call this function with null.
     * @param description the new description.
     * @return this.
     */
    public InteractiveMessageBuilder addDescription(String description){
        this.description = description;
        return this;
    }
    /**
     * Adds a single line to the message, followed by a line break.
     * @param line the line that is added to the current page.
     * @return this.
     */
    public InteractiveMessageBuilder addLine(String line){
        current_page.append(line).append("\n");
        return this;
    }
    /**
     * Adds all lines over multiple pages.
     * @param lines all lines that are added.
     * @param elements_per_page the elements each page should have.
     * @return this.
     */
    public InteractiveMessageBuilder addLines(Collection<?> lines, int elements_per_page){
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
    public InteractiveMessageBuilder nextPage(){
        embeds.add(createPage());
        current_page.setLength(0);
        fields.clear();
        return this;
    }
    /**
     * Adds a new page to the message.
     * @param embed the complete page.
     * @return this.
     */
    public InteractiveMessageBuilder addPage(EmbedBuilder embed){
        embeds.add(embed);
        return this;
    }
    /**
     * Sets the thumbnail for every page.
     * @param thumbnail the URL of the thumnbnail.
     * @return this.
     */
    public InteractiveMessageBuilder setThumbnail(String thumbnail){
        this.thumbnail = thumbnail;
        return this;
    }

    /**
     * Adds a single field to the current page.
     * The field won't be inlined.
     * @param name the name of the field.
     * @param content the value of the field.
     * @return this
     */
    public InteractiveMessageBuilder addField(String name, String content){
        return addField(name, content, false);
    }

    /**
     * Adds a single field to the current page.
     * @param name the name of the field.
     * @param content the value of the field.
     * @param inline whether the field is inlined
     * @return this
     */
    public InteractiveMessageBuilder addField(String name, String content, boolean inline){
        fields.add(new MessageEmbed.Field(name, content, inline));
        return this;
    }
    /**
     * Sets the title for all pages after the size is known.
     * @param embeds the pages.
     * @return this.
     */
    private InteractiveMessageBuilder updateTitle(List<EmbedBuilder> embeds){
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
    private InteractiveMessageBuilder updateThumbnail(List<EmbedBuilder> embeds){
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
            embed.setDescription(description);
        }
        fields.forEach(embed::addField);
        embed.addField("",current_page.toString(),false);
        return embed;
    }
    /**
     * @return the final message.
     */
    public InteractiveMessage build(){
        List<EmbedBuilder> output = new ArrayList<>(embeds.size()+1);
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
        return new InteractiveMessage(author,pages,comm);
    }
}
