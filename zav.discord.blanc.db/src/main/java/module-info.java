open module zav.discord.blanc.db {
  requires static org.eclipse.jdt.annotation;
  
  requires org.apache.commons.lang3;
  requires org.apache.logging.log4j;
  requires zav.discord.blanc.databind;
  
  requires transitive java.sql;
  requires transitive java.inject;
  
  exports zav.discord.blanc.db;
  exports zav.discord.blanc.db.sql;
}