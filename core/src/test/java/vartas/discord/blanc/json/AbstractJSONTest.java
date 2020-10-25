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

package vartas.discord.blanc.json;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import vartas.discord.blanc.*;
import vartas.discord.blanc.mock.GuildMock;
import vartas.discord.blanc.mock.RoleMock;
import vartas.discord.blanc.mock.TextChannelMock;
import vartas.discord.blanc.mock.WebhookMock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractJSONTest extends AbstractTest {
    public static Path JSON;

    public static Path GUILD_PATH;
    public static JSONObject JSON_GUILD;
    public static Guild GUILD;

    public static JSONObject JSON_ROLE;
    public static Role ROLE;

    public static JSONObject JSON_TEXT_CHANNEL;
    public static TextChannel TEXT_CHANNEL;
    public static JSONObject JSON_WEBHOOK;
    public static Webhook WEBHOOK;

    @BeforeAll
    public static void setUpAll() throws IOException{
        JSON = RESOURCES.resolve("json");

        GUILD_PATH = JSON.resolve("Guild.json");
        JSON_GUILD = parse(GUILD_PATH);
        GUILD = JSONGuild.fromJson(new GuildMock(), GUILD_PATH);

        JSON_ROLE = parse(JSON.resolve("Role.json"));
        ROLE = JSONRole.fromJson(new RoleMock(), JSON_ROLE);

        JSON_TEXT_CHANNEL = parse(JSON.resolve("TextChannel.json"));
        TEXT_CHANNEL = JSONTextChannel.fromJson(new TextChannelMock(), JSON_TEXT_CHANNEL);

        JSON_WEBHOOK = parse(JSON.resolve("Webhook.json"));
        WEBHOOK = JSONWebhook.fromJson(new WebhookMock(), JSON_WEBHOOK);
    }

    private static JSONObject parse(Path path) throws IOException {
        String content = Files.readString(path);
        return new JSONObject(content);
    }
}
