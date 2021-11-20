module zav.discord.blanc.api {
  requires static org.eclipse.jdt.annotation;
  
  requires com.fasterxml.jackson.databind;
  requires java.sql;
  requires org.apache.commons.lang3;
  requires org.apache.logging.log4j;
  requires zav.discord.blanc.databind;
  
  exports zav.discord.blanc.db;
  exports zav.discord.blanc.job;
  exports zav.discord.blanc;
  
  // SQL statements
  opens guild;
  opens role;
  opens textchannel;
  opens user;
  opens webhook;
}