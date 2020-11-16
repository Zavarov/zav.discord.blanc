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

package vartas.discord.blanc.mock;

import org.apache.http.HttpStatus;
import vartas.reddit.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SubredditMock extends Subreddit {
    public ACTION action = ACTION.NO_EXCEPTION;
    public List<Submission> submissions = new ArrayList<>();

    public enum ACTION{
        UNSUCCESSFUL_EXCEPTION,
        FORBIDDEN_EXCEPTION,
        CLIENT_EXCEPTION,
        SERVER_EXCEPTION,
        UNKNOWN_EXCEPTION,
        NO_EXCEPTION
    }

    @Override
    public List<Submission> getSubmissions(Instant inclusiveFrom, Instant exclusiveTo) throws UnsuccessfulRequestException {
        switch(action){
            case UNSUCCESSFUL_EXCEPTION:
                throw new UnsuccessfulRequestException();
            case FORBIDDEN_EXCEPTION:
                throw new ClientException(HttpStatus.SC_FORBIDDEN, "Forbidden");
            case CLIENT_EXCEPTION:
                throw new ClientException(HttpStatus.SC_UNAUTHORIZED, "Unauthorized");
            case SERVER_EXCEPTION:
                throw new ServerException(HttpStatus.SC_BAD_GATEWAY, "Bad Gateway");
            case UNKNOWN_EXCEPTION:
                throw new RuntimeException();
            default:
                return submissions;
        }
    }
}
