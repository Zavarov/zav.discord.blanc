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

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import net.dean.jraw.models.Submission;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.apache.commons.text.StringEscapeUtils;

/**
 * This class transforms a Reddit submission into a readable Discord message.
 * @author u/Zavarov
 */
public abstract class SubmissionMessage {
    /**
     * Never create instances of this class.
     */
    private SubmissionMessage(){}
    /**
     * @param submission a submission.
     * @return true if the submission is marked as NSFW.
     */
    private static boolean isNsfw(Submission submission){
        return submission.isNsfw() 
                || submission.getTitle().toLowerCase(Locale.ENGLISH).contains("[nsfw]") 
                || (submission.getLinkFlairText() != null && submission.getLinkFlairText().toLowerCase(Locale.ENGLISH).contains("nsfw"));
    }
    /**
     * @param submission a submission.
     * @return true if the submission is marked as a spoiler.
     */
    private static boolean isSpoiler(Submission submission){
        return submission.isSpoiler() 
                || submission.getTitle().toLowerCase(Locale.ENGLISH).contains("[spoiler]")
                || (submission.getLinkFlairText() != null && submission.getLinkFlairText().toLowerCase(Locale.ENGLISH).contains("spoiler"));
    }
    /**
     * the first 8 bit represent blue, the second 8 bit green and the third 8
     * bit represent red.
     * @param hash the input hash value that is used to generate a color.
     * @return a color that represents this hash code.
     */
    private static Color createColor(int hash){
        int r = (hash >> 16) & 0xFF;
        int g = (hash >> 8) & 0xFF;
        int b = hash & 0xFF;
        return new Color(r,g,b);
    }
    /**
     * Adds tags and warnings to the title and removes any HTML encodings.
     * @param submission the submission.
     * @return the refined title of the submission.
     */
    private static String formatTitle(Submission submission){
        StringBuilder builder = new StringBuilder();
        if(submission.getLinkFlairText() != null){
            builder.append(String.format("[%s] ",submission.getLinkFlairText()));
        }
        builder.append(StringEscapeUtils.unescapeHtml4(submission.getTitle()));
        if(isNsfw(submission) && !builder.toString().toLowerCase(Locale.ENGLISH).contains("[nsfw]")){
            builder.append(" [NSFW]");
        }
        if(isSpoiler(submission) && !builder.toString().toLowerCase(Locale.ENGLISH).contains("[spoiler]")){
            builder.append(" [Spoiler]");
        }
        return builder.toString();
    }
    /**
     * Adds the selftext and the thumbnail of the submission to the embed, if
     * it is neither marked as NSFW nor marked as spoiler.
     * @param embed the embed builder.
     * @param submission the submission.
     */
    private static void setDescription(EmbedBuilder embed, Submission submission){
        if(!isNsfw(submission) && !isSpoiler(submission)){
            //If the picture is considered to be SFW, attach it
            if(submission.getThumbnail() != null && EmbedBuilder.URL_PATTERN.matcher(submission.getThumbnail()).matches()){
                embed.setThumbnail(submission.getThumbnail());
            }
            String text = StringEscapeUtils.unescapeHtml4(submission.getSelfText());
            text = text.length() > MessageEmbed.TEXT_MAX_LENGTH ? text.substring(0, MessageEmbed.TEXT_MAX_LENGTH-3)+"..." : text;
            embed.setDescription(text);
        }
    }
    /**
     * Sets the color of the embed based on the author name.
     * @param embed the embed builder.
     * @param submission the submission.
     */
    private static void setColor(EmbedBuilder embed, Submission submission){
        if(isNsfw(submission)){
            embed.setColor(Color.RED);
        }else if(isSpoiler(submission)){
            //(0,0,0) is treated as transparency by Discord
            embed.setColor(new Color(1,0,0));
        }else{
            embed.setColor(createColor(submission.getAuthor().hashCode()));
        }
    }
    /**
     * Sets the time of when the submission was created.
     * @param embed the embed builder.
     * @param submission the submission.
     */
    private static void setTimestamp(EmbedBuilder embed, Submission submission){
        embed.setTimestamp(LocalDateTime.ofInstant(submission.getCreated().toInstant(),ZoneId.of("UTC")));
    }
    /**
     * Sets a direct link to the content of the submission.
     * @param embed the embed builder.
     * @param submission the submission.
     */
    private static void setAuthor(EmbedBuilder embed, Submission submission){
        embed.setAuthor("source", submission.getUrl(),null);
    }
    /**
     * Adds the relevent content to the embed.
     * @param embed the embed builder.
     * @param submission the submission.
     */
    private static void createContent(EmbedBuilder embed, Submission submission){
        setAuthor(embed, submission);
        setTimestamp(embed, submission);
        setColor(embed, submission);
        setDescription(embed, submission);
    }
    /**
     * Adds the title of the submission to both the embed and the message,
     * in case the embeds have been disabled.
     * @param message the message builder.
     * @param embed the embed builder.
     * @param submission the submission.
     */
    private static void createTitle(MessageBuilder message, EmbedBuilder embed, Submission submission){
        //The message
        message.append(String.format("New submission from %s in `r/%s`\n",submission.getAuthor(),submission.getSubreddit()));
        message.append("\n");
        message.append(String.format("<https://redd.it/%s>",submission.getId()));
        
        String url = "https://www.reddit.com"+submission.getPermalink();
        //Creates the title and truncates it, if it is getting too long.
        String title = formatTitle(submission);
        title = title.length() > MessageEmbed.TITLE_MAX_LENGTH ? title.substring(0,MessageEmbed.TITLE_MAX_LENGTH-3)+"..." : title;
        
        embed.setTitle(title, url);
    }
    /**
     * Transforms a Reddit submission into a Discord message.
     * @param submission a submission.
     * @return a framework of a finished message, containg all important information of the submission.
     */
    public static MessageBuilder create(Submission submission){
        MessageBuilder message = new MessageBuilder();
        EmbedBuilder embed = new EmbedBuilder();
        
        createTitle(message,embed,submission);
        createContent(embed,submission);
        
        message.setEmbed(embed.build());
        return message;
    }
}