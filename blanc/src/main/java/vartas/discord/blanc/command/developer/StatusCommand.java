/*
 * Copyright (C) 2017 u/Zavarov
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

package vartas.discord.blanc.command.developer;

import net.dv8tion.jda.core.entities.Message;
import org.atteo.evo.inflector.English;
import vartas.discord.bot.api.communicator.CommunicatorInterface;
import vartas.discord.bot.command.entity._ast.ASTEntityType;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;
import java.util.List;

/**
 * This command shows the status of the bot.
 */
public class StatusCommand extends StatusCommandTOP {
    /**
     * A constant for MebiByte to make the used memory easier to read.
     */
    protected final static int MEBI = 1 << 20;

    public StatusCommand(Message source, CommunicatorInterface communicator, List<ASTEntityType> parameters) throws IllegalArgumentException, IllegalStateException {
        super(source, communicator, parameters);
    }

    /**
     * Collects all the information and sends it.
     */
    @Override
    public void run(){
        StringBuilder builder = new StringBuilder();
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();

        long used = (memory.getHeapMemoryUsage().getUsed() + memory.getNonHeapMemoryUsage().getUsed())/MEBI;
        long allocated = (memory.getHeapMemoryUsage().getCommitted()+ memory.getNonHeapMemoryUsage().getCommitted())/MEBI;
        long available = (memory.getHeapMemoryUsage().getMax()+ memory.getNonHeapMemoryUsage().getMax())/MEBI;
        double systemload = os.getSystemLoadAverage()/os.getAvailableProcessors();
        
        builder.append(String.format("%s v%s\n\n",environment.config().getBotName(),environment.config().getVersion()));
        builder.append(String.format("`Architecture :` %s\n",os.getArch()));
        builder.append(String.format("`OS           :` %s\n",os.getName()));
        builder.append(String.format("`Version      :` %s\n",os.getVersion()));
        builder.append("\n");
        builder.append("Memory (in MiB)\n");
        builder.append("`Used | Allocated | Available | Ratio`\n");
        builder.append(String.format("`%-4d | %-9d | %-9d | %-2.1f%%`\n",used,allocated,available,100.0*used/allocated));
        builder.append("\n");
        builder.append(String.format("Currently running on %d Threads with a system load of %.1f%% per core.\n",Thread.activeCount(),100*systemload));
        builder.append(String.format("Ping of %d ms.\n",communicator.jda().getPing()));
        Duration duration = Duration.ofMillis(runtime.getUptime());
        builder.append(String.format("Running for %d %s %d %s %d %s.\n",
                duration.toDays(),English.plural("day",(int)duration.toDays()),
                duration.toHours() % 24, English.plural("hour",(int)(duration.toHours() % 24)),
                duration.toMinutes() % 60,English.plural("minute",(int)(duration.toMinutes() % 60))));
        builder.append("\n");
        builder.append(String.format("%s\n",runtime.getVmName()));
        builder.append(String.format("`Vendor  :` %s\n",runtime.getVmVendor()));
        builder.append(String.format("`Version :` %s\n",runtime.getVmVersion()));
        builder.append("\n");
        builder.append(String.format("%s\n",runtime.getSpecName()));
        builder.append(String.format("`Vendor  :` %s\n",runtime.getSpecVendor()));
        builder.append(String.format("`Version :` %s\n",runtime.getSpecVersion()));
        communicator.send(channel,builder.toString());
    }
}
