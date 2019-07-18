package vartas.discord.bot.io.status;

import org.assertj.core.data.Index;
import org.junit.Before;
import org.junit.Test;
import vartas.discord.bot.io.status._ast.ASTStatusArtifact;

import java.util.List;

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
public class StatusTest {
    ASTStatusArtifact ast;

    @Before
    public void setUp(){
        ast = StatusHelper.parse("src/test/resources/status.stt");
    }

    @Test
    public void testStatusMessageList(){
        List<String> list = ast.getStatusMessageList();

        assertThat(list).hasSize(5);
        assertThat(list).contains("Status0", Index.atIndex(0));
        assertThat(list).contains("Status1", Index.atIndex(1));
        assertThat(list).contains("Status2", Index.atIndex(2));
        assertThat(list).contains("Status3", Index.atIndex(3));
        assertThat(list).contains("Status4", Index.atIndex(4));
    }
}