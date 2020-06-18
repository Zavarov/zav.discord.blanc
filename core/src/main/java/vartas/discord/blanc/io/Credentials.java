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

package vartas.discord.blanc.io;

public class Credentials extends CredentialsTOP{
    /**
     * The Maven build process automatically stores the POM version in the manifest when creating the Jar.<br>
     * However, during the test phase, this value is not specified. Therefore a "Test version" is generated.
     * @return this program's version.
     */
    public String getVersion(){
        String packageVersion = getClass().getPackage().getImplementationVersion();
        return packageVersion == null ? getBotName() + "#Test" : packageVersion;
    }
}
