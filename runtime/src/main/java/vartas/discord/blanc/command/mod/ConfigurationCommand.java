package vartas.discord.blanc.command.mod;

import com.google.common.base.Preconditions;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

public class ConfigurationCommand extends ConfigurationCommandTOP{
    @Override
    public void run() {
        //TODO
        throw new UnsupportedOperationException();
        /*
        builder = new InteractiveMessageBuilder(author.getUser(), shard);
        printBlacklist();
        printPrefix();
        builder.nextPage();
        printRedditFeeds();
        printSelfassignableRoles();
        shard.queue(channel, builder.build());

         */
    }

    /*
    private void printBlacklist(){
        String pattern = configuration.getPattern().map(Pattern::pattern).orElse("");
        builder.addField("Blacklist", pattern);
    }

    private void printPrefix(){
        String prefix = configuration.getPrefix().orElse("");
        builder.addField("Prefix", prefix);
    }

    private void printRedditFeeds(){
        Map<String, Collection<TextChannel>> map = configuration.resolve(Configuration.LongType.SUBREDDIT, guild::getTextChannelById).asMap();
        map.forEach((subreddit, channels) -> {
            builder.addDescription(subreddit);
            channels.stream().map(IMentionable::getAsMention).forEach(builder::addLine);
            builder.nextPage();
        });

    }

    private void printSelfassignableRoles(){
        Map<String, Collection<Role>> map = configuration.resolve(Configuration.LongType.SELFASSIGNABLE, guild::getRoleById).asMap();
        map.forEach((tag, roles) -> {
            builder.addDescription(tag);
            roles.stream().map(IMentionable::getAsMention).forEach(builder::addLine);
            builder.nextPage();
        });
    }
     */
}
