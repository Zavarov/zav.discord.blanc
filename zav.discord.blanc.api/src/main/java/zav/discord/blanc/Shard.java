package zav.discord.blanc;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import zav.discord.blanc.databind.io.Credentials;

public class Shard {
  private final Credentials credentials;
  
  public Shard() throws IOException {
    ObjectMapper om = new ObjectMapper();
    this.credentials = om.readValue(new File("credentials.json"), Credentials.class);
  }
  
  public Credentials getCredentials() {
    return credentials;
  }
}
