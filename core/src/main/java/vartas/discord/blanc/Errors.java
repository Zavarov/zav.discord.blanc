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

import javax.annotation.Nonnull;

@Nonnull
public enum Errors {
    //File Access
    INVALID_JSON_FILE(0x1000, "The given file is not a valid JSON object."),
    INVALID_FILE(0x1001, "The given file couldn't be accessed."),
    //Server Communication
    UNSUCCESSFUL_REDDIT_REQUEST(0x2000, "The request was unsuccessful due to an unknown error."),
    REDDIT_CLIENT_ERROR(0x2001, "The request was rejected due to a client error."),
    REDDIT_SERVER_ERROR(0x2002, "The request was rejected due to a server error."),
    REDDIT_API_ERROR(0x2003, "The request was rejected due to an unknown API error."),
    DISCORD_TIMEOUT(0x2100, "The Discord server took to long to respond to the request"),
    UNKNOWN_RESPONSE(0x2200, "The server returned an unknown error."),
    //Type handling
    UNKNOWN_ENTITY(0x3000, "The given entity could not be resolved."),
    MULTIPLE_ENTITIES_BY_NAME(0x3001, "Multiple entities with the same name were found."),
    //Commands
    INSUFFICIENT_RANK(0x4000, "The command couldn't be created due to an insufficient rank."),
    INSUFFICIENT_PERMISSION(0x4001, "The command couldn't be created due to insufficient permissions."),
    INSUFFICIENT_ATTACHMENTS(0x4002, "The command requires at least one attachment."),
    //Resolve Discord Entities
    UNKNOWN_GUILD(0x5000, "The specified guild couldn't be found."),
    UNKNOWN_TEXTCHANNEL(0x5001, "The specified text channel couldn't be found."),
    UNKNOWN_USER(0x5002, "The specified user couldn't be found."),
    UNKNOWN_MEMBER(0x5003, "The specified member couldn't be found."),
    UNKNOWN_ROLE(0x5004, "The specified role couldn't be found."),
    //Guild attributes
    INVALID_PATTERN(0x6000, "The blacklist pattern couldn't be compiled.")
    ;

    @Nonnull
    private final String description;
    private final long code;

    Errors(long code, @Nonnull String description){
        this.code = code;
        this.description = description;
    }

    @Override
    @Nonnull
    public String toString(){
        return String.format("0x%s (%s)", Long.toHexString(code), description);
    }
}
