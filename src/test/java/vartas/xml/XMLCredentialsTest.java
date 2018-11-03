/*
 * Copyright (C) 2018 u/Zavarov
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
package vartas.xml;

import java.io.File;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author u/Zavarov
 */
public class XMLCredentialsTest {
    XMLCredentials credentials;
    
    @Before
    public void setUp(){
        credentials = XMLCredentials.create(new File("src/test/resources/credentials.xml"));
    }
    @Test
    public void getDiscordShardsTest(){
        assertEquals(credentials.getDiscordToken(),"token");
    }
    @Test
    public void getIdTest() {
        assertEquals(credentials.getId(),"id");
    }
    @Test
    public void getSecretTest() {
        assertEquals(credentials.getSecret(),"secret");
    }
    @Test
    public void getAppidTest() {
        assertEquals(credentials.getAppid(),"appid");
    }
    @Test
    public void getUserTest() {
        assertEquals(credentials.getUser(),"user");
    }
    @Test
    public void getRedirectTest() {
        assertEquals(credentials.getRedirect(),"redirect");
    }
    @Test
    public void getScopeTest() {
        assertEquals(credentials.getScope(),"scope");
    }
    @Test
    public void getPlatformTest() {
        assertEquals(credentials.getPlatform(),"platform");
    }
    @Test
    public void getVersionTest() {
        assertEquals(credentials.getVersion(),"version");
    }
}