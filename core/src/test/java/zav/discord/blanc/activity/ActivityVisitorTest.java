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

package zav.discord.blanc.activity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.AbstractTest;
import zav.discord.blanc.activity.ActivityVisitor;

import static org.assertj.core.api.Assertions.assertThat;

public class ActivityVisitorTest extends AbstractTest {
    ActivityVisitor visitor;
    @BeforeEach
    public void setUp(){
        visitor = new ActivityVisitor();
    }

    @Test
    public void testVisitor(){
        shard.accept(visitor);
        assertThat(guild.getActivity().asMapActivity()).isNotEmpty();
    }
}
