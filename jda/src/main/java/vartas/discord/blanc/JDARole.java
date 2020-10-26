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
import vartas.discord.blanc.factory.RoleFactory;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;

public class JDARole extends Role{
    /**
     * The date pretty printer.
     */
    protected static final DateTimeFormatter DATE = DateTimeFormatter.RFC_1123_DATE_TIME;
    @Nonnull
    private final net.dv8tion.jda.api.entities.Role role;
    @Nonnull
    private JDARole(@Nonnull net.dv8tion.jda.api.entities.Role role){
        this.role = role;
    }

    public static Role create(@Nonnull net.dv8tion.jda.api.entities.Role role){
        return RoleFactory.create(
                () -> new JDARole(role),
                Optional.empty(),
                role.getIdLong(),
                role.getName()
        );
    }

    @Override
    public String getAsMention(){
        return role.getAsMention();
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
        addId(messageEmbed);
        addCreated(messageEmbed);
        addPosition(messageEmbed);
        addMembersCount(messageEmbed);
        addColor(messageEmbed);

        return messageEmbed;
    }

    private void setTitle(MessageEmbed messageEmbed){
        messageEmbed.setTitle(role.getName());
    }

    private void addId(MessageEmbed messageEmbed){
        messageEmbed.addFields("ID", role.getId());
    }

    private void addCreated(MessageEmbed messageEmbed){
        OffsetDateTime created = role.getTimeCreated();
        String date = DATE.format(created);
        int days = (int)DAYS.between(created.atZoneSameInstant(ZoneOffset.UTC).toLocalDate(), LocalDate.now(ZoneOffset.UTC));

        messageEmbed.addFields("Created", String.format("%s (%d %s ago)", date, days, English.plural("day", days)));
    }

    private void addPosition(MessageEmbed messageEmbed){
        messageEmbed.addFields("Position", role.getPosition());
    }

    private void addMembersCount(MessageEmbed messageEmbed){
        messageEmbed.addFields("#Members", role.getGuild().getMembersWithRoles(role).size());
    }

    private void addColor(MessageEmbed messageEmbed){
        Color color = role.getColor();

        if(color != null){
            messageEmbed.addFields("Color", String.format("0x%02X%02X%02X",
                    color.getRed(),
                    color.getGreen(),
                    color.getBlue())
            );
        }
    }
}
