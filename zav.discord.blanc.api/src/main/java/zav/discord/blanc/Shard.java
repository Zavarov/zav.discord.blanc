package zav.discord.blanc;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import zav.discord.blanc.databind.io.CredentialsValueObject;

public class Shard {
  private final CredentialsValueObject credentials;
  
  public Shard() throws IOException {
    ObjectMapper om = new ObjectMapper();
    this.credentials = om.readValue(new File("credentials.json"), CredentialsValueObject.class);
  }
  
  public CredentialsValueObject getCredentials() {
    return credentials;
  }
}
