module zav.discord.blanc.db {
  requires static org.eclipse.jdt.annotation;
  
  requires org.apache.commons.lang3;
  requires org.apache.logging.log4j;
  requires zav.discord.blanc.databind;
  
  requires transitive java.sql;
  
  exports zav.discord.blanc.db;
  
  opens guild;
  opens textchannel;
  opens user;
  opens webhook;
}