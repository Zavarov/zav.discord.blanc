/*
 * Copyright (c) 2022 Zavarov.
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
