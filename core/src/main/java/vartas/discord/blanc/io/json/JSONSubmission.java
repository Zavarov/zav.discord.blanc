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

package vartas.discord.blanc.io.json;

import org.json.JSONObject;
import vartas.reddit.Submission;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class JSONSubmission extends Submission {
    private static final String AUTHOR = "author";
    private static final String TITLE = "title";
    private static final String SCORE = "score";
    private static final String IS_NSFW = "isNsfw";
    private static final String IS_SPOILER = "isSpoiler";
    private static final String ID = "id";
    private static final String CREATED = "created";


    public static JSONSubmission of(Path submissionPath, Instant date) throws IOException {
        return of(Files.readString(submissionPath.resolve(date.toString())));
    }

    public static JSONSubmission of(String content){
        return of(new JSONObject(content));
    }

    public static JSONSubmission of(JSONObject jsonObject){
        JSONSubmission jsonSubmission = new JSONSubmission();

        jsonSubmission.setAuthor(jsonObject.getString(AUTHOR));
        jsonSubmission.setTitle(jsonObject.getString(TITLE));
        jsonSubmission.setScore(jsonObject.getInt(SCORE));
        jsonSubmission.setNsfw(jsonObject.getBoolean(IS_NSFW));
        jsonSubmission.setSpoiler(jsonObject.getBoolean(IS_SPOILER));
        jsonSubmission.setId(jsonObject.getString(ID));
        jsonSubmission.setCreated(Instant.parse(jsonObject.getString(CREATED)));

        return jsonSubmission;
    }


    @Override
    public String getPermaLink() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getUrl() {
        throw new UnsupportedOperationException();
    }
}
