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

package zav.discord.blanc._json;

import org.json.JSONArray;
import org.json.JSONObject;
import zav.discord.blanc.Guild;
import zav.discord.blanc.Message;
import zav.discord.blanc.io._json.JSONCredentials;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * This class is responsible for (de-)serializing {@link Guild} instances.
 * <p>
 * For each {@link Guild}, a file corresponding to its id is created, containing custom configurations such as the
 * prefix and blacklist. Those have to be stored in order to preserve the data in case the application restarts.
 */
@Nonnull
public class JSONGuild extends JSONGuildTOP {
    /**
     * Calculates the file name for the serialized {@link Guild}. The name consists of its id, to ensure that the file
     * name is unique, as well as an identifier, marking it as {@link Guild}, in order to distinguish it from other
     * serialized types.
     * @param source The {@link Guild} that is serialized.
     * @return The file name of the serialized {@link Guild}.
     * @see #getFileName(long) 
     */
    @Nonnull
    public static String getFileName(@Nonnull Guild source){
        return getFileName(source.getId());
    }

    /**
     * Calculates the file name for the serialized {@link Guild}. The name consists of its id, to ensure that the file
     * name is unique, as well as an identifier, marking it as {@link Guild}, in order to distinguish it from other
     * serialized types.
     * @param id The id of the {@link Guild} that is serialized.
     * @return The file name of the serialized {@link Guild}.
     * @see #getFileName(Guild) 
     */
    @Nonnull
    protected static String getFileName(long id){
        return "g" + Long.toUnsignedString(id) + ".json";
    }

    /**
     * Calculates the qualified path of folder containing the serialized {@link Guild}. All JSON files correlating to
     * a specific {@link Guild} are stored in a common folder identified by the {@link Guild} id.
     * @param id The {@link Guild} id used to determine the folder of containing the serialized {@link Guild} file.
     * @return The qualified path of the folder containing the serialized {@link Guild}. No attempts are made to verify
     *         that this directory actually exists.
     */
    @Nonnull
    protected static Path getFileDirectory(long id){
        return JSONCredentials.CREDENTIALS.getJsonDirectory().resolve(Long.toUnsignedString(id));
    }

    /**
     * Deserializes the specified {@link Guild}. The file indicated by the <code>id</code> is read and its content
     * written into the provided {@link Guild} instance.
     * @param target The {@link Guild} the deserialized content is written into.
     * @param id The id of the {@link Guild} that is deserialized.
     * @return An instance of the deserialized {@link Guild}.
     * @throws IOException If the corresponding file couldn't be read.
     */
    @Nonnull
    public static Guild fromJson(@Nonnull Guild target, long id) throws IOException {
        Path filePath = getFileDirectory(id).resolve(getFileName(id));
        return fromJson(target, filePath);
    }

    /**
     * Extracts the most recent activity of the {@link Guild}. Since the activity is not serialized, this method
     * immediately returns without altering the state of the provided {@link Guild}.
     * @param source The JSON file from which the activity is retrieved.
     * @param target The {@link Guild} instance in which the activity is stored.
     */
    @Override
    protected void $fromActivity(@Nonnull JSONObject source, @Nonnull Guild target){
        //Omitted
    }

    /**
     * Stores the {@link Guild} activity in the corresponding JSON file. However, doing so causes an unnecessary
     * overhead without providing much of value. As such, this data is serialized.
     * @param source The {@link Guild} instance containing the most recent activity.
     * @param target The JSON object the activity is written into.
     */
    @Override
    protected void $toActivity(@Nonnull Guild source, @Nonnull JSONObject target){
        //Omitted
    }

    /**
     * Extracts the pattern describing all blacklisted expressions. The pattern itself is stored as a sequence of
     * expressions, in order to simplify the process of adding and removing. Once all expressions have been read, the
     * pattern is compiled. Every {@link Message} whose content is accepted by this pattern is removed automatically.
     * @param source The JSON file from which the blacklist is retrieved.
     * @param target The {@link Guild} instance in which the blacklist is stored.
     */
    @Override
    protected void $fromBlacklist(@Nonnull JSONObject source, @Nonnull Guild target){
        JSONArray jsonBlacklist = source.optJSONArray(BLACKLIST);
        if(jsonBlacklist != null) {
            for (int i = 0; i < jsonBlacklist.length(); ++i) {
                target.addBlacklist(jsonBlacklist.getString(i));
            }
            target.compilePattern();
        }
    }

    /**
     * Stores the blacklist of the provided {@link Guild} in the corresponding JSON file. The pattern is stored as an
     * array, with its value representing the individual expressions in the list.
     * @param source The {@link Guild} instance from which the blacklist is retrieved.
     * @param target The JSON object the blacklist is written into.
     */
    @Override
    protected void $toBlacklist(@Nonnull Guild source, @Nonnull JSONObject target){
        JSONArray jsonBlacklist = new JSONArray();
        for(String entry : source.getBlacklist())
            jsonBlacklist.put(entry);
        target.put(BLACKLIST, jsonBlacklist);
    }
}
