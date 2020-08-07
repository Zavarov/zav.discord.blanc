package vartas.discord.blanc.command.mod;

import vartas.discord.blanc.MessageEmbed;
import vartas.discord.blanc.Role;
import vartas.discord.blanc.TextChannel;
import vartas.discord.blanc.factory.MessageEmbedFactory;

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
