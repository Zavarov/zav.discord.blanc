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

package vartas.discord.bot.visitor.reddit;

import vartas.discord.bot.reddit.RedditFeed;

public interface RedditFeedVisitor extends SubredditFeedVisitor{

    default void visit(RedditFeed feed){}

    default void traverse(RedditFeed feed) {
        feed.accept(this);
    }

    default void handle(RedditFeed feed){
        visit(feed);
        traverse(feed);
    }
}
