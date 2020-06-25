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
package vartas.discord.message;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import org.atteo.evo.inflector.English;
import vartas.discord.entities.Shard;
import vartas.discord.message.builder.InteractiveMessageBuilder;

import java.awt.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * This class creates a Discord message displaying the information of a Discord
 * role.
 */
public abstract class RoleMessage {
    /**
     * Never create instances of this class.
     */
    protected RoleMessage(){}
    /**
     * The formatter for the dates.
     */
    protected static final DateTimeFormatter DATE = DateTimeFormatter.RFC_1123_DATE_TIME;
    /**
     * Sets the description of the current page of the message.
     * @param builder the message builder.
     * @param role the role in question.
     */
    private static void addDescription(InteractiveMessageBuilder builder, Role role){
        builder.addDescription(role.getName());
    }
    /**
     * Adds the id of the role to the message.
     * @param builder the message builder.
     * @param role the role in question.
     */
    private static void addId(InteractiveMessageBuilder builder, Role role){
        builder.addField("ID", role.getId());
    }
    /**
     * Adds the number of days since the role was created to the message.
     * @param builder the message builder.
     * @param role the role in question.
     */
    private static void addCreated(InteractiveMessageBuilder builder, Role role){
        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
        OffsetDateTime created = role.getTimeCreated();
        String date = formatter.format(created);
        int days = (int)DAYS.between(created.atZoneSameInstant(ZoneOffset.UTC).toLocalDate(), LocalDate.now(ZoneOffset.UTC));

        builder.addField("Created", String.format("%s (%d %s ago)", date, days, English.plural("day", days)));
    }
    /**
     * Adds the position of the role in the hierarchy to the message.
     * @param builder the message builder.
     * @param role the role in question.
     */
    private static void addPosition(InteractiveMessageBuilder builder, Role role){
        builder.addField("Position", role.getPosition());
    }
    /**
     * Adds the number of members with this role to the message.
     * @param builder the message builder.
     * @param role the role in question.
     */
    private static void addMembersCount(InteractiveMessageBuilder builder, Role role){
        builder.addField("#Members", role.getGuild().getMembersWithRoles(role).size());
    }
    /**
     * Adds the color of the role to the message if the color isn't the default one.
     * @param builder the message builder.
     * @param role the role in question.
     */
    private static void addColor(InteractiveMessageBuilder builder, Role role){
        Optional<Color> colorOpt = Optional.ofNullable(role.getColor());

        colorOpt.ifPresent(color -> {
            //0 represents a transparent color
            if (role.getColorRaw() != Role.DEFAULT_COLOR_RAW) {
                builder.addLine(String.format("%-10s : 0x%02X%02X%02X",
                        "Color",
                        color.getRed(),
                        color.getGreen(),
                        color.getBlue())
                );
            }
        });
    }
    /**
     * Adds the effective ranks this role gives to the message.
     * @param builder the message builder.
     * @param role the role in question.
     */
    private static void addPermissions(InteractiveMessageBuilder builder, Role role, TextChannel channel){
        builder.addDescription("Role permissions");
        Permission.getPermissions(PermissionUtil.getEffectivePermission(channel,role))
                .stream()
                .sorted(Comparator.comparing(Permission::getOffset))
                .map(Permission::getName)
                .forEach(builder::addLine);
    }
    /**
     * Adds all members with this role to the message.
     * @param builder the message builder.
     * @param role the role in question.
     */
    private static void addMembersWithRole(InteractiveMessageBuilder builder, Role role){
        builder.addDescription("Members with this role");
        List<String> members = role
                .getGuild()
                .getMembersWithRoles(role)
                .stream()
                .sorted(Comparator.comparing(ISnowflake::getIdLong))
                .map(IMentionable::getAsMention)
                .collect(Collectors.toList());
        builder.addLines(members, 10);
    }
    
    /**
     * Shows the information about the specified role and submits it.
     * @param author the user who triggered this command.
     * @param role the role in question.
     * @param channel the channel the member is in.
     * @param comm the communicator in the shard the message is in.
     * @return an interactive message displaying the roles information
     */
    public static InteractiveMessage create(User author, Role role, TextChannel channel, Shard comm){
        InteractiveMessageBuilder builder = new InteractiveMessageBuilder(author, comm);

        addDescription(builder, role);
        addId(builder,role);
        addCreated(builder,role);
        addPosition(builder,role);
        addMembersCount(builder,role);
        addColor(builder, role);
        builder.nextPage();

        addPermissions(builder, role, channel);
        builder.nextPage();
        
        addMembersWithRole(builder, role);
        return builder.build();
    }
}
