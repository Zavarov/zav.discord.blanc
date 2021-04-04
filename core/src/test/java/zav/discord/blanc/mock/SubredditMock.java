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

package zav.discord.blanc.mock;

import zav.jra.Link;
import zav.jra.Parameter;
import zav.jra.Subreddit;
import zav.jra.exceptions.ForbiddenException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SubredditMock extends Subreddit {
    public List<Link> links = new ArrayList<>();
    public IOException getNewLinksException = null;

    public SubredditMock(String name){
        setName(name);
        setDisplayName(name);
    }

    @Override
    public Stream<Link> getNewLinks(Parameter... params) throws IOException{
        if(getNewLinksException == null){
            return links.stream();
        }else{
            throw getNewLinksException;
        }
    }
}
