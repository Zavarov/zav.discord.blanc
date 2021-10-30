module zav.discord.blanc.api {
  requires java.sql;
  requires zav.discord.blanc.databind;
  requires com.fasterxml.jackson.databind;
  requires org.apache.commons.lang3;
  requires org.apache.logging.log4j;
  
  exports zav.discord.blanc.db;
  exports zav.discord.blanc.job;
  exports zav.discord.blanc;
  
  opens guild;
  opens role;
  opens textchannel;
  opens user;
  opens webhook;
}