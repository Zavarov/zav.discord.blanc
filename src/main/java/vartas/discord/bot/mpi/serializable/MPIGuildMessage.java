package vartas.discord.bot.mpi.serializable;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.Serializable;
import java.util.Optional;

public interface MPIGuildMessage extends Serializable{
    long getGuildId();

    long getChannelId();

    default Optional<TextChannel> getMessageChannel(JDA jda) {
        Optional<Guild> guildOpt = Optional.ofNullable(jda.getGuildById(getGuildId()));
        return guildOpt.flatMap(guild -> Optional.ofNullable(guild.getTextChannelById(getChannelId())));
    }
}
