package zav.discord.blanc.db.internal;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlQuery {
  private static final Logger LOGGER = LogManager.getLogger(SqlQuery.class);
  private static final String LOGGER_REGEX = "\\s{2,}|" + System.lineSeparator();
  public static final String GUILD_DB = "jdbc:sqlite:Guild.db";
  public static final String ROLE_DB = "jdbc:sqlite:Role.db";
  public static final String TEXTCHANNEL_DB = "jdbc:sqlite:TextChannel.db";
  public static final String WEBHOOK_DB = "jdbc:sqlite:WebHook.db";
  public static final String USER_DB = "jdbc:sqlite:User.db";
  protected String db;
  
  public SqlQuery(String db) {
    this.db = db;
  }
  
  // insert/update/delete
  // returns number of affected records
  public int update(String sqlStmt, Object... args) throws SQLException {
    synchronized (this) {
      try (Connection conn = DriverManager.getConnection(db)) {
        try (Statement stmt = conn.createStatement()) {
          stmt.setQueryTimeout(60); // timeout in sec
          return executeUpdate(stmt, readFromFile(sqlStmt, args));
        }
      }
    }
  }
  
  private int executeUpdate(Statement stmt, String sql) throws SQLException {
    int affectedRows = stmt.executeUpdate(sql);
    LOGGER.info("{} row(s) affected.", affectedRows);
    return affectedRows;
  }
  
  public List<SqlObject> query(String sqlStmt, Object... args) throws SQLException {
    synchronized (this) {
      try (Connection conn = DriverManager.getConnection(db)) {
        try (Statement stmt = conn.createStatement()) {
          stmt.setQueryTimeout(60); // timeout in sec
          return executeQuery(stmt, readFromFile(sqlStmt, args));
        }
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
    return result;
  }
  
  public int insert(String sqlStmt, SqlConsumer consumer) throws SQLException {
    synchronized (this) {
      try (Connection conn = DriverManager.getConnection(db)) {
        try (PreparedStatement stmt = conn.prepareStatement(readFromFile(sqlStmt))) {
          stmt.setQueryTimeout(60); // timeout in sec
          return executeInsert(stmt, consumer);
        }
      }
    }
  }
  
  private int executeInsert(PreparedStatement stmt, SqlConsumer consumer) throws SQLException {
    consumer.accept(stmt);
    
    int affectedRows = stmt.executeUpdate();
    LOGGER.info("{} row(s) affected.", affectedRows);
    return affectedRows;
  }
  
  private static String readFromFile(String path, Object... args) {
    try {
      InputStream is = SqlQuery.class.getClassLoader().getResourceAsStream(path);
      
      if(is == null) {
        throw new FileNotFoundException(path);
      }
      
      byte[] content = is.readAllBytes();
      
      String result = new String(content, StandardCharsets.UTF_8);
      
      // Substitute arguments
      result = String.format(result, args);
      
      // Prettify log message
      LOGGER.info(result.replaceAll(LOGGER_REGEX, StringUtils.SPACE));

      return result;
    } catch(IOException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }
  
  public static String serialize(Object obj) {
    try {
      ObjectMapper om = new ObjectMapper();
      return om.writeValueAsString(obj);
    } catch (IOException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }
  
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
  
  public static <T> T unmarshal(Object obj, Class<T> target) {
    ObjectMapper om = new ObjectMapper();
    // The database may contain more entries than what is required by the POJO
    om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return om.convertValue(obj, target);
  }
}
