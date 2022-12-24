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

package zav.discord.blanc.runtime.command.dev;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import zav.discord.blanc.command.AbstractCommand;
import zav.discord.blanc.command.CommandManager;
import zav.discord.blanc.databind.Rank;

/**
 * This command shows the status of the bot.
 */
public class StatusCommand extends AbstractCommand {
  /**
   * A constant for MebiByte to make the used memory easier to read.
   */
  private static final int MEBI = 1 << 20;
  private static final double GIGA = 1e9;
  
  private final EmbedBuilder messageEmbed = new EmbedBuilder();
  private final SystemInfo systemInfo = new SystemInfo();
  private final HardwareAbstractionLayer hardware = systemInfo.getHardware();
  private final SlashCommandEvent event;
  
  private OperatingSystem os;
  private OSProcess process;
  private RuntimeMXBean runtime;
  
  private GlobalMemory memory;
  private CentralProcessor cpu;
  private List<CentralProcessor.LogicalProcessor> processors;
  
  private String cpuName;
  private String logicalProcessorCount;
  private String physicalProcessorCount;
  private String availableProcessors;
  private String systemLoad;
  private String cpuTemperature;
  private String cpuVoltage;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The command-specific manager.
   */
  public StatusCommand(SlashCommandEvent event, CommandManager manager) {
    super(manager);
    this.event = event;
    
    os = systemInfo.getOperatingSystem();
    process = os.getProcess(os.getProcessId());
    runtime = ManagementFactory.getRuntimeMXBean();
  
    memory = hardware.getMemory();
    cpu = hardware.getProcessor();
    processors = cpu.getLogicalProcessors();

    logicalProcessorCount = Integer.toString(cpu.getLogicalProcessorCount());
    physicalProcessorCount = Integer.toString(cpu.getPhysicalProcessorCount());
    systemLoad = (100.0 * cpu.getSystemLoadAverage(1)[0] / cpu.getLogicalProcessorCount()) + "%";
  
    OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
    availableProcessors = Integer.toString(os.getAvailableProcessors());
  
    CentralProcessor.ProcessorIdentifier identifier = cpu.getProcessorIdentifier();
    cpuName = identifier.getName();

    Sensors sensors = hardware.getSensors();
    cpuTemperature = Double.toString(sensors.getCpuTemperature());
    cpuVoltage = Double.toString(sensors.getCpuVoltage());
  }
  
  @Override
  public Rank getRequiredRank() {
    return Rank.DEVELOPER;
  }

  @Override
  public void run() {
    messageEmbed.setTitle(os.toString());
    // CPU
    messageEmbed.addField("CPU Name", cpuName, false);
    messageEmbed.addField("Logical", logicalProcessorCount, true);
    messageEmbed.addField("Physical", physicalProcessorCount, true);
    messageEmbed.addField("Available", availableProcessors, true);
    messageEmbed.addField("System Load", systemLoad, true);
    messageEmbed.addField("Frequency (GHz)", getFrequencies(), true);
    // Sensors
    messageEmbed.addField("CPU Temperature (°C)", cpuTemperature, false);
    messageEmbed.addField("CPU Voltage (V)", cpuVoltage, true);
    // OS
    messageEmbed.addField("Uptime", getUptime(), false);
    // JVM
    messageEmbed.addField("JVM", runtime.getSpecName(), false);
    messageEmbed.addField("Vendor", runtime.getSpecVendor(), true);
    messageEmbed.addField("Version", runtime.getSpecVersion(), true);
    // Global Uptime
    messageEmbed.addField("Global Memory", getGlobalMemory(), false);
    
    event.replyEmbeds(messageEmbed.build()).complete();
  }
  
  private String getFrequencies() {
    StringBuilder frequencyBuilder = new StringBuilder();
    long [] frequencies = cpu.getCurrentFreq();

    for (int i = 0; i < Math.min(processors.size(), frequencies.length); ++i) {
      int processNumber = processors.get(i).getProcessorNumber();
      double frequency = frequencies[i] / GIGA;
      String pattern = "`[%-2d] %.2f`%n";
      
      frequencyBuilder.append(String.format(pattern, processNumber, frequency));
    }

    return frequencyBuilder.toString();
  }
  
  private String getUptime() {
    Duration duration = Duration.ofMillis(process.getUpTime());
    
    long days = duration.toDays();
    long hours = duration.toHoursPart();
    long minutes = duration.toMillisPart() % 24;
    String pattern = "Running for %d day(s), %d hour(s) and %d minute(s).";
    
    return String.format(pattern, days, hours, minutes);
  }
  
  private String getGlobalMemory() {
    long total = memory.getTotal() / MEBI;
    long free = memory.getAvailable() / MEBI;
    long used = total - free;
    double ratio = (100.0 * used) / total;
    String keys = "`Total | Used  | Free  | Ratio`";
    String pattern = "%s%n`%-5d | %-5d | %-5d | %-4.1f%%`";
    
    return String.format(pattern, keys, total, used, free, ratio);
  }
}
