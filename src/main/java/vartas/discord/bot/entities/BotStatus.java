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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class BotStatus {
    protected Random random = new Random();
    protected List<String> status = new ArrayList<>();

    public synchronized void add(String element){
        status.add(element);
    }

    public synchronized Optional<String> get(){
        if(status.isEmpty())
            return Optional.empty();

        int index = random.nextInt(status.size());
        return Optional.of(status.get(index));
    }
}
