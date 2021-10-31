/*
 * Copyright (c) 2021 Zavarov.
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

package zav.discord.blanc.runtime.command.dev;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;
import java.util.List;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import zav.discord.blanc.Rank;
import zav.discord.blanc.command.AbstractCommand;
import zav.discord.blanc.databind.message.MessageEmbed;
import zav.discord.blanc.databind.message.Title;

/**
 * This command shows the status of the bot.
 */
public class StatusCommand extends AbstractCommand {
  /**
   * A constant for MebiByte to make the used memory easier to read.
   */
  protected static final int MEBI = 1 << 20;
  protected static final double GIGA = 1e9;
  
  private final MessageEmbed messageEmbed = new MessageEmbed();
  private final SystemInfo systemInfo = new SystemInfo();
  private final HardwareAbstractionLayer hardware = systemInfo.getHardware();
  
  public StatusCommand() {
    super(Rank.DEVELOPER);
  }
  
  @Override
  public void run() {
    setTitle();
    printCpu();
    printSensors();
    printOs();
    printMemory();
    printJvm();
    channel.send(messageEmbed);
  }
  
  private void setTitle() {
    OperatingSystem os = systemInfo.getOperatingSystem();
    messageEmbed.withTitle(new Title().withName(os.toString()));
  }
  
  private void printCpu() {
    OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
    CentralProcessor cpu = hardware.getProcessor();
    CentralProcessor.ProcessorIdentifier identifier = cpu.getProcessorIdentifier();
    
    messageEmbed.addField("CPU Name", identifier.getName());
    messageEmbed.addField("Logical", cpu.getLogicalProcessorCount(), true);
    messageEmbed.addField("Physical", cpu.getPhysicalProcessorCount(), true);
    messageEmbed.addField("Available", os.getAvailableProcessors(), true);
    messageEmbed.addField("System Load", 100.0 * cpu.getSystemLoadAverage(1)[0] / cpu.getLogicalProcessorCount() + "%", true);
    
    StringBuilder frequencyBuilder = new StringBuilder();
    List<CentralProcessor.LogicalProcessor> processors = cpu.getLogicalProcessors();
    long [] frequencies = cpu.getCurrentFreq();
    for (int i = 0; i < Math.min(processors.size(), frequencies.length); ++i) {
      frequencyBuilder.append(String.format("`[%-2d] %.2f`\n", processors.get(i).getProcessorNumber(), frequencies[i] / GIGA));
    }
    messageEmbed.addField("Frequency (GHz)", frequencyBuilder.toString(), true);
  }
  
  private void printSensors() {
    Sensors sensors = hardware.getSensors();
    messageEmbed.addField("CPU Temperature (°C)", sensors.getCpuTemperature(), true);
    messageEmbed.addField("CPU Voltage (V)", sensors.getCpuVoltage(), true);
  }
  
  private void printOs() {
    OperatingSystem os = systemInfo.getOperatingSystem();
    OSProcess process = os.getProcess(os.getProcessId());
    Duration duration = Duration.ofMillis(process.getUpTime());
    messageEmbed.addField(
          "Uptime",
          String.format(
                "Running for %d day(s), %d hour(s) and %d minute(s).\n",
                duration.toDays(),
                duration.toHours() % 24,
                duration.toMinutes() % 60
          ),
          false
    );
  }
  
  private void printJvm() {
    RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
    messageEmbed.addField("JVM", runtime.getSpecName());
    messageEmbed.addField("Vendor", runtime.getSpecVendor(), true);
    messageEmbed.addField("Version", runtime.getSpecVersion(), true);
  }
  
  private void printMemory() {
    GlobalMemory memory = hardware.getMemory();
    OperatingSystem os = systemInfo.getOperatingSystem();
    OSProcess process = os.getProcess(os.getProcessId());
    long total = memory.getTotal() / MEBI;
    long free = memory.getAvailable() / MEBI;
    long used = process.getResidentSetSize() / MEBI;
    double ratio = (100.0 * used) / total;
    
    String memoryMessage = "`Total | Used  | Free  | Ratio`\n"
          + String.format("`%-5d | %-5d | %-5d | %-4.1f%%`\n", total, used, free, ratio);
    messageEmbed.addField("Global Memory", memoryMessage, false);
  }
}
