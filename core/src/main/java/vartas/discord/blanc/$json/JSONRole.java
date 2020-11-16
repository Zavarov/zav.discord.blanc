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

package vartas.discord.blanc.$json;

import org.json.JSONObject;
import vartas.discord.blanc.Guild;
import vartas.discord.blanc.Member;
import vartas.discord.blanc.Role;
import vartas.discord.blanc.io.$json.JSONCredentials;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * This class is responsible for (de-)serializing {@link Role} instances.
 * <p>
 * For each {@link Role}, a file corresponding to its id is created, containing an optional group. This group is used
 * in combination with self-assignable roles. A {@link Member} can only ever have one {@link Role} in a group. If they
 * attempt to get a {link Role} but already have a {@link Role} in the same group, old one is replaced by the new
 * {@link Role}.
 */
@Nonnull
public class JSONRole extends JSONRoleTOP {
    /**
     * The key corresponding to the optional {@link Role} group.
     */
    @Nonnull
    public static final String GROUP = "group";

    /**
     * Calculates the file name for the serialized {@link Role}. The name consists of its id, to ensure that the file
     * name is unique, as well as an identifier, marking it as {@link Role}, in order to distinguish it from other
     * serialized types.
     * @param source The {@link Role} that is serialized.
     * @return The file name of the serialized {@link Role}.
     * @see #getFileName(long)
     */
    @Nonnull
    public static String getFileName(@Nonnull Role source){
        return getFileName(source.getId());
    }
    /**
     * Calculates the file name for the serialized {@link Role}. The name consists of its id, to ensure that the file
     * name is unique, as well as an identifier, marking it as {@link Role}, in order to distinguish it from other
     * serialized types.
     * @param id The id of the {@link Role} that is serialized.
     * @return The file name of the serialized {@link Role}.
     * @see #getFileName(Role)
     */
    @Nonnull
    protected static String getFileName(long id){
        return "r" + Long.toUnsignedString(id) + ".json";
    }

    /**
     * Calculates the qualified path of folder containing the serialized {@link Role}. All JSON files correlating to
     * a specific {@link Guild} are stored in a common folder identified by the {@link Guild} id.
     * @param id The {@link Guild} id used to determine the folder of containing the serialized {@link Role} file.
     * @return The qualified path of the folder containing the serialized {@link Role}. No attempts are made to verify
     *         that this directory actually exists.
     */
    @Nonnull
    protected static Path getFileDirectory(long id){
        return JSONCredentials.CREDENTIALS.getJsonDirectory().resolve(Long.toUnsignedString(id));
    }

    /**
     * Deserializes the specified {@link Role}. The file indicated by the <code>id</code> is read and its content written into
     * the provided {@link Role} instance. The location of the file is determined by the {@link Guild}.
     * @param target The {@link Role} the deserialized content is written into.
     * @param guild The {@link Guild} specifying the location of the JSON file.
     * @param id The id of the {@link Role} that is deserialized.
     * @return An instance of the deserialized {@link Role}.
     * @throws IOException If the corresponding file couldn't be read.
     */
    @Nonnull
    public static Role fromJson(@Nonnull Role target, @Nonnull Guild guild, long id) throws IOException {
        Path filePath = getFileDirectory(guild.getId()).resolve(getFileName(id));
        return fromJson(target, filePath);
    }

    /**
     * Extracts the optional {@link Role} group from the JSON file. A {@link Role} can be made self-assignable by
     * assigning it to such a group. For each group, a {@link Member} may at most have a single {@link Role} assigned at
     * a time.
     * @param source The JSON file from which the group is retrieved.
     * @param target The {@link Role} instance in which the group is stored.
     */
    @Override
    protected void $fromGroup(@Nonnull JSONObject source, @Nonnull Role target){
        target.setGroup(Optional.ofNullable(source.optString(GROUP)));
    }

    /**
     * Stores the {@link Role} group, if present, in the corresponding JSON file.
     * @param source The {@link Role} instance that may be a member a group.
     * @param target The JSON object the group is written into.
     */
    @Override
    protected void $toGroup(@Nonnull Role source, @Nonnull JSONObject target){
        source.ifPresentGroup(group -> target.put(GROUP, group));
    }
}
