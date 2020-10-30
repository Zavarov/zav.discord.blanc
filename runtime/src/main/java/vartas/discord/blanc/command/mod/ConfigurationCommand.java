/*
 * Copyright (c) 2020 Zavarov
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

package vartas.discord.blanc.command.mod;

import vartas.discord.blanc.$factory.MessageEmbedFactory;
import vartas.discord.blanc.MessageEmbed;
import vartas.discord.blanc.Role;
import vartas.discord.blanc.TextChannel;

import java.util.Locale;

public class ConfigurationCommand extends ConfigurationCommandTOP{
    @Override
    public void run() {
        switch(getModule().toLowerCase(Locale.ENGLISH)){
            case "blacklist":
                showBlacklist();
                break;
            case "prefix":
                showPrefix();
                break;
            case "reddit":
                showSubredditFeeds();
                break;
            case "selfassignable":
                showSelfassignableRoles();
                break;
            default:
                get$TextChannel().send("Unknown module: %s", getModule());
        }
    }

    private void showBlacklist(){
        MessageEmbed messageEmbed = MessageEmbedFactory.create();

        String value = get$Guild().getBlacklist().stream().reduce((u,v) -> u + "\n" + v).orElse("");
        messageEmbed.addFields("Blacklist", value);

        get$TextChannel().send(messageEmbed);
    }

    private void showPrefix(){
        MessageEmbed messageEmbed = MessageEmbedFactory.create();

        String value = get$Guild().getPrefix().orElse("");
        messageEmbed.addFields("Prefix", value);

        get$TextChannel().send(messageEmbed);
    }

    private void showSubredditFeeds(){
        MessageEmbed messageEmbed = MessageEmbedFactory.create();

        for(TextChannel textChannel : get$Guild().valuesChannels()){
            String value = textChannel.getSubreddits().stream().reduce((u,v) -> u + "\n" + v).orElse("");
            //Only print channels that link to at least one subreddit
            if(!value.isBlank())
                messageEmbed.addFields(textChannel.getName(), value);
        }

        get$TextChannel().send(messageEmbed);
    }

    private void showSelfassignableRoles(){
        MessageEmbed messageEmbed = MessageEmbedFactory.create();

        for(Role role : get$Guild().valuesRoles()){
            if(role.isPresentGroup()){
                messageEmbed.addFields(role.getName(), role.getGroup().orElseThrow());
            }
        }

        get$TextChannel().send(messageEmbed);
    }
}
