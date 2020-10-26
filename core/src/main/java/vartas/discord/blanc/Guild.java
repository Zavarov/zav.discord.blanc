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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * The internal representation of a Discord guild.
 */
@Nonnull
public abstract class Guild extends GuildTOP {
    /**
     * This class' logger.
     */
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    /**
     * The pattern derived from the blacklisted words.
     * May be null if there are no banned words.
     */
    @Nullable
    private Pattern pattern;

    /**
     * @return an {@link Optional} containing the pattern for blacklisted words.
     */
    @Nonnull
    public Optional<Pattern> getPattern(){
        return Optional.ofNullable(pattern);
    }

    @Override
    public Guild getRealThis() {
        return this;
    }

    /**
     * Generates a pattern based on the blacklisted words. The pattern will accept any word that is accepted
     * by at least one blacklisted expression. In other words, any text sequence that is matched by the pattern
     * should be removed.<br>
     * Choosing this approach instead of matching the word with each expression one by one will drastically
     * reduce the overhead of the operation.
     */
    public void compilePattern() {
        try {
            if (isEmptyBlacklist()) {
                pattern = null;
            } else {
                pattern = Pattern.compile(getBlacklist().stream().reduce((u, v) -> u + "|" + v).orElseThrow());
            }
        } catch (PatternSyntaxException e) {
            log.error(Errors.INVALID_PATTERN.toString(), e.toString());
        }
    }

    /**
     * Checks whether the specified {@link Member} can interact with the specified {@link Role}.<br>
     * Interacting usually means assigning this role to others, which is only possible when the
     * member has is the {@link Guild} owner or has a {@link Role} higher than the one that is modified.<br>
     * Another content would be the modification of the {@link Permission Permissions} associating
     * with the {@link Role}, which is only possible under similar conditions.
     * @param member the {@link Member} associated with the {@link Role}.
     * @param role the {@link Role} associated with the {@link Member}.
     * @return true if the {@link Member} can interact with the {@link Role}.
     */
    public abstract boolean canInteract(@Nonnull Member member, @Nonnull Role role);

    /**
     * Checks whether the specified {@link Member} can interact with the specified {@link TextChannel}.<br>
     * In this context, interacting means being able to see the {@link TextChannel} and to be able
     * to send messages in it. This is only possible, when the {@link Member} both has {@link Permission#SEND_MESSAGES}
     * and {@link Permission#VIEW_CHANNEL}.
     * @see Permission#VIEW_CHANNEL
     * @see Permission#SEND_MESSAGES
     * @param member the {@link Member} associated with the {@link TextChannel}.
     * @param textChannel the {@link TextChannel} associated with the {@link Member}.
     * @return true if the {@link Member} can interact with the {@link TextChannel}.
     */
    public abstract boolean canInteract(@Nonnull Member member, @Nonnull TextChannel textChannel);
}
