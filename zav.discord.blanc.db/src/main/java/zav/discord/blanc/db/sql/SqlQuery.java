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

package zav.discord.blanc.db.sql;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Wrapper class for performing SQL requests on the local database.
 */
@Singleton
public class SqlQuery {
  private static final Logger LOGGER = LogManager.getLogger();
  // Remove unnecessary spaces & line breaks
  private static final String LOGGER_REGEX = "\\s{2,}|" + System.lineSeparator();
  public static final Path ENTITY_DB_PATH = Paths.get("db/Entity.db");
  public static final String ENTITY_DB = "jdbc:sqlite:" + ENTITY_DB_PATH;
  
  /**
   * Used to insert, update or delete elements from the database.<br>
   * The {@code sqlStmt} may contain wildcards (e.g. indicated with a '%s' for strings). Those
   * elements are substituted by {@code args} in the order they appear in the expression.<br>
   * This method is thread-safe, s.t. only one thread is allowed to modify the database at a time.
   *
   * @param sqlStmt The path to the SQL statement that is going to be executed.
   * @param args Additional arguments for the SQL statement.
   * @return The number of affected records.
   * @throws SQLException If a database error occurred.
   */
  public synchronized int update(String sqlStmt, Object... args) throws SQLException {
    try (Connection conn = DriverManager.getConnection(ENTITY_DB)) {
      try (Statement stmt = conn.createStatement()) {
        stmt.setQueryTimeout(60); // timeout in sec
        return executeUpdate(stmt, readFromFile(sqlStmt, args));
      }
    }
  }
  
  
  /**
   * Used to insert, update or delete elements from the database.<br>
   * The {@code consumer} may be used to dynamically add elements to the request (e.g. the values
   * when storing an entity) which may only be known during runtime.<br>
   * This method is thread-safe, s.t. only one thread is allowed to modify the database at a time.
   *
   * @param sqlStmt The path to the SQL statement that is going to be executed.
   * @param consumer Additional actions that need to be performed on the statement before the
   *                 request can be executed.
   * @return The number of affected records.
   * @throws SQLException If a database error occurred.
   */
  public synchronized int update(String sqlStmt, SqlConsumer consumer) throws SQLException {
    try (Connection conn = DriverManager.getConnection(ENTITY_DB)) {
      try (PreparedStatement stmt = conn.prepareStatement(readFromFile(sqlStmt))) {
        stmt.setQueryTimeout(60); // timeout in sec
        return executeUpdate(stmt, consumer);
      }
    }
  }
  
  private int executeUpdate(Statement stmt, String sql) throws SQLException {
    int affectedRows = stmt.executeUpdate(sql);
    
    LOGGER.info("{} row(s) affected.", affectedRows);
    
    return affectedRows;
  }
  
  private int executeUpdate(PreparedStatement stmt, SqlConsumer consumer) throws SQLException {
    consumer.accept(stmt);
    
    int affectedRows = stmt.executeUpdate();
    
    LOGGER.info("{} row(s) affected.", affectedRows);
    
    return affectedRows;
  }
  
  /**
   * Used to retrieve elements from the database.<br>
   * The {@code sqlStmt} may contain wildcards (e.g. indicated with a '%s' for strings). Those
   * elements are substituted by {@code args} in the order they appear in the expression.
   *
   * @param sqlStmt The path to the SQL statement that is going to be executed.
   * @param args Additional arguments for the SQL statement.
   * @return An immutable list containing the retrieved elements .
   * @throws SQLException If a database error occurred.
   */
  public synchronized List<SqlObject> query(String sqlStmt, Object... args) throws SQLException {
    try (Connection conn = DriverManager.getConnection(ENTITY_DB)) {
      try (Statement stmt = conn.createStatement()) {
        stmt.setQueryTimeout(60); // timeout in sec
        return executeQuery(stmt, readFromFile(sqlStmt, args));
      }
    }
  }
  
  private List<SqlObject> executeQuery(Statement stmt, String sql) throws SQLException {
    List<SqlObject> result = new ArrayList<>();
    
    try (ResultSet response = stmt.executeQuery(sql)) {
      ResultSetMetaData md = response.getMetaData();
      // Iterate over all rows
      while (response.next()) {
        SqlObject sqlObj = new SqlObject();
        // Insert all columns in the object
        for (int i = 1; i <= md.getColumnCount(); ++i) {
          sqlObj.put(md.getColumnLabel(i), response.getObject(i));
        }
        result.add(sqlObj);
        
        LOGGER.info("Queried {},", sqlObj);
      }
    }
    return List.copyOf(result);
  }
  
  private static String readFromFile(String path, Object... args) {
    try (@Nullable InputStream is = SqlQuery.class.getClassLoader().getResourceAsStream(path)) {
      if (is == null) {
        throw new FileNotFoundException(path);
      }
      
      byte[] content = is.readAllBytes();
      
      String result = new String(content, StandardCharsets.UTF_8);
      
      // Substitute arguments
      result = String.format(result, args);
      
      // Prettify log message
      LOGGER.info(result.replaceAll(LOGGER_REGEX, StringUtils.SPACE));

      return result;
    } catch (IOException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }
  
  /**
   * Transforms the provided object into a JSON string.
   *
   * @param obj A Java object to be serialized.
   * @return The serialized object in JSON format.
   */
  public static String marshal(Object obj) {
    try {
      ObjectMapper om = new ObjectMapper();
      return om.writeValueAsString(obj);
    } catch (IOException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }
  
  /**
   * Transforms the provided object into the desired Java object.
   *
   * @param obj A serialized version of the desired object.
   * @return The deserialized Java object.
   */
  public static <T> T unmarshal(Object obj, Class<T> target) {
    ObjectMapper om = new ObjectMapper();
    // The database may contain more entries than what is required by the POJO
    om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return om.convertValue(obj, target);
  }
  
  /**
   * Deserializes a list of strings. SQL doesn't seem to handle arrays very well, hence why those
   * are encoded into a single String, which is then split into a list of Strings during runtime.
   *
   * @param obj A serialized list of Strings.
   * @return The deserialized list of Strings.
   */
  public static List<String> deserialize(Object obj) {
    try {
      ObjectMapper om = new ObjectMapper();
      ArrayNode node = (ArrayNode) om.readTree(obj.toString());
      
      List<String> result = new ArrayList<>();
      
      node.elements().forEachRemaining(child -> result.add(child.asText()));
      
      return List.copyOf(result);
    } catch (IOException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }
}
