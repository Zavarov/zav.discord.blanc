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

package vartas.discord.blanc;

import org.atteo.evo.inflector.English;
import vartas.discord.blanc.factory.MessageEmbedFactory;
import vartas.discord.blanc.factory.UserFactory;
import vartas.discord.blanc.io.json.JSONRanks;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;

public class JDAUser extends User{
    /**
     * The formatter for the dates.
     */
    private static final SimpleDateFormat DATE = new SimpleDateFormat("EEE, d MMM ''yy z", Locale.ENGLISH);

    @Nonnull
    private final net.dv8tion.jda.api.entities.User user;

    public JDAUser(@Nonnull net.dv8tion.jda.api.entities.User user){
        this.user = user;
    }

    @Nonnull
    public static User create(net.dv8tion.jda.api.entities.User user){
        return UserFactory.create(
                () -> new JDAUser(user),
                Optional.empty(),                           //Private Channel
                JSONRanks.RANKS.getRanks().get(user.getIdLong()),
                user.getIdLong(),
                user.getName()
        );
    }

    @Override
    public Optional<PrivateChannel> getChannel(){
        return Optional.of(JDAPrivateChannel.create(user.openPrivateChannel().complete()));
    }

    @Override
    public String getAsMention(){
        return user.getAsMention();
    }

    //------------------------------------------------------------------------------------------------------------------
    //
    //      Printable
    //
    //------------------------------------------------------------------------------------------------------------------

    @Override
    public MessageEmbed toMessageEmbed(){
        MessageEmbed messageEmbed = MessageEmbedFactory.create();

        setTitle(messageEmbed);
        setThumbnail(messageEmbed);
        addId(messageEmbed);
        addCreated(messageEmbed);

        return messageEmbed;
    }

    private void setTitle(MessageEmbed messageEmbed){
        messageEmbed.setTitle(user.getName() + "#" + user.getDiscriminator());
    }

    private void setThumbnail(MessageEmbed messageEmbed){
        messageEmbed.setThumbnail(user.getEffectiveAvatarUrl());
    }

    protected void addId(MessageEmbed messageEmbed){
        messageEmbed.addFields("ID", user.getId());
    }

    protected void addCreated(MessageEmbed messageEmbed){
        int days = (int)DAYS.between(user.getTimeCreated().toLocalDate(), LocalDate.now());
        messageEmbed.addFields(
                "Created",
                String.format("%s\n(%d %s ago)",
                        DATE.format(Date.from(user.getTimeCreated().toInstant())),
                        days,
                        English.plural("day", days)
                ),
                true
        );

    }
}
