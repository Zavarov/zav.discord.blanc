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

import com.google.common.base.Preconditions;
import net.dv8tion.jda.api.JDA;
import vartas.discord.blanc.visitor.RedditVisitor;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutionException;

public class JDAShard extends Shard {
    private final JDA jda;

    public JDAShard
            (
                    @Nonnull RedditVisitor redditVisitor,
                    @Nonnull JDA jda
            )
    {
        super(redditVisitor);
        this.jda = jda;
    }

    public JDAShard
        (
            @Nonnull RedditVisitor redditVisitor,
            @Nonnull StatusMessageRunnable statusMessageRunnable,
            @Nonnull JDA jda
        )
    {
        super(redditVisitor, statusMessageRunnable);
        this.jda = jda;
    }

    @Override
    @Nonnull
    public Guild getGuilds(@Nonnull Long key){
        try{
            return getGuilds(key, () -> {
                net.dv8tion.jda.api.entities.Guild guild = jda.getGuildById(key);
                //TODO Internal Error
                Preconditions.checkNotNull(guild);
                return JDAGuild.create(guild);
            });
        }catch(ExecutionException e){
            //TODO Internal error
            throw new RuntimeException("Internal error: " + e.getMessage());
        }
    }
}
