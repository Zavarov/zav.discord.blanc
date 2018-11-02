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

package vartas.xml;

import java.io.File;
import vartas.xml.strings.XMLStringMap;

/**
 * A helper class for all the commands that are registered.
 * @author u/Zavarov
 */
public class XMLCommand extends XMLStringMap<String>{
    private static final long serialVersionUID = 1L;
    /**
     * @param key the class path.
     * @return the command string for this command.
     */
    public String getCommand(String key){
        return get(key);
    }
    /**
     * Adds a new entry for the command.
     * @param key the class path.
     * @param command  the command.
     */
    public void addCommand(String key, String command){
        super.put(key,command);
    }
    /**
     * Creates a new command file from an XML file.
     * @param reference the XML file.
     * @return the commands containing all elements in the XML document.
     */
    public static XMLCommand create(File reference){
        XMLCommand command = new XMLCommand();
        command.putAll(XMLStringMap.create(reference));
        return command;
    }
}
