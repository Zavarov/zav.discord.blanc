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

package zav.discord.blanc._json;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import zav.discord.blanc.AbstractTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractJSONTest extends AbstractTest {
    public JSONObject jsonGuild;
    public JSONObject jsonRole;
    public JSONObject jsonTextChannel;
    public JSONObject jsonWebhook;

    @BeforeEach
    public void setUp() throws IOException{
        Path json = RESOURCES.resolve("10");

        jsonGuild = parse(json.resolve("g10.json"));
        jsonRole = parse(json.resolve("r20.json"));
        jsonTextChannel = parse(json.resolve("t30.json"));
        jsonWebhook = parse(json.resolve("w40.json"));
    }

    private static JSONObject parse(Path path) throws IOException {
        String content = Files.readString(path);
        return new JSONObject(content);
    }
}
