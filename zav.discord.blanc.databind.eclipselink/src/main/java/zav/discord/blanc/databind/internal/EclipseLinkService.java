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

package zav.discord.blanc.databind.internal;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * This class should be used in an OSGi environment to initialize the
 * Persistence provider with an instance of EclipseLink.<br>
 * Due to incompatibilities with SPI, it is not guaranteed that the provider can
 * be found via {@link Persistence#createEntityManagerFactory(String)}, hence
 * its implementation has to be used directly.
 */
@Component(immediate = true)
public class EclipseLinkService {
  private EntityManagerFactory factory;
  
  /**
   * This method is automatically called by the OSGi framework when this fragment
   * is started. It creates a new {@link EntityManagerFactory} instance and adds
   * it to {@link PersistenceUtil}.
   */
  @Activate
  public void activate() {
    Map<String, Object> map = new HashMap<>();
    map.put(PersistenceUnitProperties.CLASSLOADER, getClass().getClassLoader());
    
    PersistenceProvider persistenceProvider = new PersistenceProvider();
    String persistenceUnit = PersistenceUtil.PERSISTENCE_UNIT_NAME;
    
    factory = persistenceProvider.createEntityManagerFactory(persistenceUnit, map);
    
    PersistenceUtil.setEntityManagerFactory(factory);
  }
  
  /**
   * This method is automatically called by the OSGi framework when this fragment
   * is stopped. It deactivates the {@link EntityManagerFactory} instance and
   * removes it from {@link PersistenceUtil}.
   */
  @Deactivate
  public void deactivate() {
    factory.close();
    factory = null;
    
    PersistenceUtil.setEntityManagerFactory(factory);
  }
}
