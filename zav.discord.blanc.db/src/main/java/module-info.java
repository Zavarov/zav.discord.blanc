module zav.discord.blanc.db {
  requires static org.eclipse.jdt.annotation;
  
  requires java.sql;
  requires com.fasterxml.jackson.databind;
  requires org.apache.commons.lang3;
  requires org.apache.logging.log4j;
  requires zav.discord.blanc.databind;
  
  exports zav.discord.blanc.db;
  
  opens guild;
  opens textchannel;
  opens user;
  opens webhook;
}