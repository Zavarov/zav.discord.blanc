package zav.discord.blanc.databind;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CredentialsTest {
  Credentials credentials;
  
  @BeforeEach
  public void setUp() throws IOException, URISyntaxException {
    URL url = Credentials.class.getClassLoader().getResource("Credentials.json");
    URI uri = url.toURI();
    File file = new File(uri);
    credentials = Credentials.read(file);
  }
  
  @Test
  public void testGetName() {
    assertEquals(credentials.getName(), "Blanc");
  }
  
  @Test
  public void testGetToken() {
    assertEquals(credentials.getToken(), "abcdef");
  }
  
  @Test
  public void testGetInviteSupportServer() {
    assertEquals(credentials.getInviteSupportServer(), "https://discord.gg/xxxxxxxxxx");
  }
  
  @Test
  public void testGetOwner() {
    assertEquals(credentials.getOwner(), 12345);
  }
  
  @Test
  public void testGetShardCount() {
    assertEquals(credentials.getShardCount(), 1);
  }
}
