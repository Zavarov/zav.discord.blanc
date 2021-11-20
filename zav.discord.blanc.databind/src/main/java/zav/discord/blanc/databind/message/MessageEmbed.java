/*
 * Copyright (c) 2021 Zavarov.
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

package zav.discord.blanc.databind.message;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Embeds are "Rich Messages" which allow additional customization such as a colored border,
 * text fields or embedded images.
 */
@NonNull
public class MessageEmbed extends MessageEmbedTOP {
  
  public void setTitle(@NonNull String name) {
    setTitle(name, null);
  }
  
  public void setTitle(@NonNull String name, @Nullable String url) {
    setTitle(new Title().withName(name).withUrl(url));
  }
  
  public void addField(@NonNull String name, @NonNull Object content) {
    addField(name, content, false);
  }
  
  public void addField(@NonNull String name, @NonNull Object content, boolean inline) {
    Field field = new Field().withName(name).withContent(content.toString()).withInline(inline);
    getFields().add(field);
  }
}
