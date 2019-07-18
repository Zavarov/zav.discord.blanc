package vartas.discord.bot.command.command.generator;

import com.ibm.icu.text.RuleBasedNumberFormat;
import de.se_rwth.commons.Joiners;
import org.atteo.evo.inflector.English;
import vartas.discord.bot.command.command._ast.ASTCommandArtifact;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

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
public class CommandGeneratorHelper {
    protected RuleBasedNumberFormat ordinalFormatter = new RuleBasedNumberFormat(Locale.ENGLISH, RuleBasedNumberFormat.ORDINAL);

    public String formatAsOrdinal(Number number){
        return ordinalFormatter.format(number);
    }

    public String pluralOf(String word, int count){
        return English.plural(word, count);
    }

    public static Path getQualifiedPath(String packageName, String fileName){
        return Paths.get(packageName.replaceAll("\\.", FileSystems.getDefault().getSeparator()), fileName + ".java");
    }

    public static String getPackage(ASTCommandArtifact ast){
        return Joiners.DOT.join(ast.getPackageList());
    }

    public static String getPackageFolder(ASTCommandArtifact ast){
        return ast.getPackageList().stream().reduce((u,v) -> u + FileSystems.getDefault().getSeparator() + v).orElse("");
    }
}
