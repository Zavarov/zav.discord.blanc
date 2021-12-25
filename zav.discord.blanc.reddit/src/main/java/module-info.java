module zav.discord.blanc.reddit {
  requires static org.eclipse.jdt.annotation;
  
  requires java.sql;
  
  requires com.google.guice;
  requires org.apache.logging.log4j;
  requires zav.discord.blanc.api;
  requires zav.discord.blanc.databind;
  requires zav.discord.blanc.db;
  requires zav.jrc.api;
  requires zav.jrc.databind;
  requires zav.jcr.listener;
  requires zav.jrc.client;
  
  exports zav.discord.blanc.reddit;
}