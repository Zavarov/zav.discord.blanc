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

import vartas.discord.blanc.$factory.AuthorFactory;
import vartas.discord.blanc.$factory.FieldFactory;
import vartas.discord.blanc.$factory.TitleFactory;

import javax.annotation.Nonnull;
import java.util.Optional;

@Nonnull
public class MessageEmbed extends MessageEmbedTOP{
    public void addFields(@Nonnull String title, @Nonnull Object content){
        addFields(title, content, false);
    }

    public void addFields(@Nonnull String title, @Nonnull Object content, boolean inline){
        addFields(FieldFactory.create(title, content, inline));
    }

    public void setTitle(@Nonnull String title){
        setTitle(TitleFactory.create(title));
    }

    public void setTitle(@Nonnull String title, @Nonnull String url){
        setTitle(TitleFactory.create(title, Optional.of(url)));
    }

    public void setAuthor(@Nonnull String name){
        setAuthor(AuthorFactory.create(name));
    }

    public void setAuthor(@Nonnull String name, @Nonnull String url){
        setAuthor(AuthorFactory.create(name, Optional.of(url)));
    }

    @Override
    public MessageEmbed getRealThis() {
        return this;
    }
}
