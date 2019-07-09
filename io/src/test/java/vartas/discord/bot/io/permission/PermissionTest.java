package vartas.discord.bot.io.permission;

import com.google.common.collect.Multimap;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * Copyright (C) 2019 Zavarov
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
public class PermissionTest {
    PermissionConfiguration permissions;

    @Before
    public void setUp(){
        String source = "src/test/resources/permission.perm";
        File reference = new File("target/test/resources/permission.perm");
        permissions = PermissionHelper.parse(source, reference);
    }

    @Test
    public void testPermissions(){
        Multimap<Long, PermissionType> multimap = permissions.getPermissions();
        assertThat(multimap.size()).isEqualTo(2);
        assertThat(multimap.get(1L)).containsExactlyInAnyOrder(PermissionType.ROOT, PermissionType.REDDIT);
    }

    @Test
    public void testUpdate(){
        String source = "target/test/resources/permission.perm";
        File reference = new File("target/test/resources/permission.perm");
        permissions = PermissionHelper.parse(source, reference);

        testPermissions();
    }
}
