/*
 * Copyright (c) 2019 Zavarov
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

package vartas.discord.bot.entities;

import org.junit.Test;
import vartas.discord.bot.AbstractTest;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

public class StatusTest extends AbstractTest {
    Status status;
    @Test
    public void getTest(){
        status = new Status();
        status.add("element");
        assertThat(status.get()).contains("element");
    }

    @Test
    public void getEmptyTest(){
        status = new Status();
        assertThat(status.get()).isEmpty();
    }

    @Test
    public void visitorTest(){
        status = new Status();
        Status.Visitor visitor = new Visitor();
        status.accept(visitor);
        visitor = new EmptyVisitor();
        status.accept(visitor);
    }

    private static class EmptyVisitor implements Status.Visitor{}

    private class Visitor implements Status.Visitor{
        @Override
        public void visit(@Nonnull Status status){
            assertThat(status).isEqualTo(StatusTest.this.status);
        }
        @Override
        public void traverse(@Nonnull Status status){
            assertThat(status).isEqualTo(StatusTest.this.status);
        }
        @Override
        public void endVisit(@Nonnull Status status){
            assertThat(status).isEqualTo(StatusTest.this.status);
        }
    }
}
