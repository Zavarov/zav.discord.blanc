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
import org.apache.http.client.HttpResponseException;
import vartas.reddit.Submission;
import vartas.reddit.Subreddit;
import vartas.reddit.TimeoutException;
import vartas.reddit.UnsuccessfulRequestException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SubredditMock extends Subreddit {
    public ACTION action = ACTION.NO_EXCEPTION;
    public List<Submission> submissions = new ArrayList<>();

    public enum ACTION{
        UNSUCCESSFUL_EXCEPTION,
        TIMEOUT_EXCEPTION,
        HTTP_EXCEPTION,
        NO_EXCEPTION
    }

    @Override
    public List<Submission> getSubmissions(Instant inclusiveFrom, Instant exclusiveTo) throws TimeoutException, UnsuccessfulRequestException, HttpResponseException {
        switch(action){
            case UNSUCCESSFUL_EXCEPTION:
                throw new UnsuccessfulRequestException();
            case TIMEOUT_EXCEPTION:
                throw new TimeoutException();
            case HTTP_EXCEPTION:
                throw new HttpResponseException(HttpStatus.SC_FORBIDDEN, "Forbidden");
            default:
                return submissions;
        }
    }
}
