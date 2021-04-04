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

package zav.discord.blanc.io;

/**
 * The credentials contain all information necessary for initializing the program. This includes passwords and
 * other information necessary for communication with the individual applications, as well as
 */
public class Credentials extends CredentialsTOP{
    /**
     * The Maven build process automatically stores the POM version in the manifest when creating the Jar.
     * <p>
     * However, during the test phase, this value is not specified. Therefore a "Test version" is generated.
     * @return This program's version.
     */
    public String getVersion(){
        String packageVersion = getClass().getPackage().getImplementationVersion();
        return packageVersion == null ? getBotName() + "#Test" : packageVersion;
    }

    /**
     * Part of the visitor pattern to grant access to the explicit implementation of the individual types.
     * @return The current instance.
     */
    @Override
    public Credentials getRealThis() {
        return this;
    }
}
