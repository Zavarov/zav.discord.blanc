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

package zav.discord.blanc.mc;

import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import net.dv8tion.jda.api.entities.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.command.IntermediateCommand;
import zav.discord.blanc.command.parser.AbstractParser;
import zav.discord.blanc.mc.callable._parser.CallableParser;

/**
 * Implementation of the Command parser using the CFG specified using MontiCore.
 */
public class MontiCoreCommandParser extends AbstractParser {
  private static final Logger LOGGER = LogManager.getLogger(MontiCoreCommandParser.class);
  private final CallableParser parser = new CallableParser();
  
  static {
    // Avoid the parser crashing the entire application upon an invalid input.
    Log.enableFailQuick(false);
  }

  @Override
  public @Nullable IntermediateCommand parse(Message message) {
    try {
      //images/files-only messages might not have any text content
      String content = message.getContentRaw();
      return parser.parse_String(content).orElse(null);
    } catch (IOException e) {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
  }
}
