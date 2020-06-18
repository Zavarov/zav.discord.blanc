package vartas.discord.blanc;

import org.junit.jupiter.api.BeforeAll;
import vartas.discord.blanc.io.Credentials;
import vartas.discord.blanc.io.json.JSONCredentials;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class AbstractTest {
    public static Path RESOURCES = Paths.get("src","test","resources");
    public static Credentials credentials;

    @BeforeAll
    public static void setUpAll() throws IOException {
        credentials = JSONCredentials.of(RESOURCES.resolve("credentials.json"));
    }
}
