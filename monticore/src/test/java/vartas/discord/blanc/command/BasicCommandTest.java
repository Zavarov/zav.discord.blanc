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

package vartas.discord.blanc.command;

import de.monticore.io.paths.ModelPath;
import vartas.discord.blanc.command._ast.ASTCommand;
import vartas.discord.blanc.command._ast.ASTCommandArtifact;
import vartas.discord.blanc.command._symboltable.CommandGlobalScope;
import vartas.discord.blanc.command._symboltable.CommandLanguage;
import vartas.monticore.cd4analysis._symboltable.CD4CodeGlobalScope;
import vartas.monticore.cd4analysis._symboltable.CD4CodeLanguage;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public abstract class BasicCommandTest {
    protected static final Path MODELS_PATH = Paths.get("src","test","resources");
    protected static final Path CLASSES_PATH = Paths.get("target", "classes-resources");
    protected static final Path TEMPLATES_PATH = Paths.get("target","templates-resources");
    protected static final Path SOURCES_PATH = Paths.get("src","main", "java");
    protected static final Path OUTPUT_PATH = Paths.get("target","generated-sources","monticore","sourcecode");

    public CommandLanguage cmdLanguage = new CommandLanguage();
    public ModelPath cmdModelPath = new ModelPath(MODELS_PATH);
    public CommandGlobalScope cmdGlobalScope = new CommandGlobalScope(cmdModelPath, cmdLanguage);
    public ModelPath cdModelPath = new ModelPath(CLASSES_PATH);
    public CD4CodeGlobalScope cdGlobalScope = new CD4CodeGlobalScope(cdModelPath, new CD4CodeLanguage());

    public ASTCommandArtifact cmdArtifact;
    public ASTCommand cmd;

    public void parseCommand(String commandName, String groupName){
        cmdArtifact = CommandGeneratorMain.parse(cmdGlobalScope, cmdModelPath, groupName);
        cmd = cmdArtifact.getCommandList().stream().filter(command -> command.getName().equals(commandName)).findAny().orElseThrow();
    }
}
