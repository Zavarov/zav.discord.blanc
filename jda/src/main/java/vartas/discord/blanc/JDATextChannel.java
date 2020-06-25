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

import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import vartas.discord.blanc.factory.TextChannelFactory;
import vartas.discord.blanc.json.JSONTextChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class JDATextChannel extends TextChannel{
    @Nonnull
    private final net.dv8tion.jda.api.entities.TextChannel textChannel;
    @Nonnull
    private JDATextChannel(@Nonnull net.dv8tion.jda.api.entities.TextChannel textChannel){
        this.textChannel = textChannel;
    }

    public static TextChannel create(net.dv8tion.jda.api.entities.TextChannel jdaTextChannel, @Nullable JSONObject jsonObject){
        TextChannel textChannel = TextChannelFactory.create(
                () -> new JDATextChannel(jdaTextChannel),
                jdaTextChannel.getIdLong(),
                jdaTextChannel.getName()
        );

        if(jsonObject != null){
            JSONArray jsonSubreddits = jsonObject.getJSONArray(JSONTextChannel.SUBREDDITS);
            for(int i = 0 ; i < jsonSubreddits.length() ; ++i)
                textChannel.addSubreddits(jsonSubreddits.getString(i));
        }

        return textChannel;
    }

    public static TextChannel create(net.dv8tion.jda.api.entities.TextChannel jdaTextChannel){
        return create(jdaTextChannel, null);
    }

    @Override
    public void send(Message message) {
        try {
            textChannel.sendMessage(MessageBuilder.buildMessage(message)).complete();
        } catch(InsufficientPermissionException e){
            throw PermissionException.of(Errors.INSUFFICIENT_PERMISSION);
        }
    }

    @Override
    public void send(byte[] bytes, String qualifiedName) {
        try {
            textChannel.sendFile(bytes, qualifiedName).complete();
        } catch(InsufficientPermissionException e){
            throw PermissionException.of(Errors.INSUFFICIENT_PERMISSION);
        }
    }
}
