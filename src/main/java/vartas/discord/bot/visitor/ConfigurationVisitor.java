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

package vartas.discord.bot.visitor;

import com.google.common.base.Preconditions;
import vartas.discord.bot.entities.Configuration;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.regex.Pattern;

public interface ConfigurationVisitor {
    default void visit(@Nonnull Configuration.LongType type, @Nonnull String key, @Nonnull Collection<Long> values){
    }
    default void traverse(@Nonnull Configuration.LongType type, @Nonnull String key, @Nonnull Collection<Long> values){
    }
    default void endVisit(@Nonnull Configuration.LongType type, @Nonnull String key, @Nonnull Collection<Long> values){
    }
    default void handle(@Nonnull Configuration.LongType type, @Nonnull String key, @Nonnull Collection<Long> values) throws NullPointerException{
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(values);
        visit(type, key, values);
        traverse(type, key, values);
        endVisit(type, key, values);
    }

    default void visit(@Nonnull Pattern pattern){
    }
    default void traverse(@Nonnull Pattern pattern){
    }
    default void endVisit(@Nonnull Pattern pattern){
    }
    default void handle(@Nonnull Pattern pattern) throws NullPointerException{
        Preconditions.checkNotNull(pattern);
        visit(pattern);
        traverse(pattern);
        endVisit(pattern);
    }

    default void visit(@Nonnull String prefix){
    }
    default void traverse(@Nonnull String prefix){
    }
    default void endVisit(@Nonnull String prefix){
    }
    default void handle(@Nonnull String prefix) throws NullPointerException{
        Preconditions.checkNotNull(prefix);
        visit(prefix);
        traverse(prefix);
        endVisit(prefix);
    }

    default void visit(@Nonnull Configuration configuration){
    }
    default void traverse(@Nonnull Configuration configuration) throws NullPointerException{
        Preconditions.checkNotNull(configuration);
        configuration.accept(this);
    }
    default void endVisit(@Nonnull Configuration configuration){
    }
    default void handle(@Nonnull Configuration configuration) throws NullPointerException{
        Preconditions.checkNotNull(configuration);
        visit(configuration);
        traverse(configuration);
        endVisit(configuration);
    }
}
