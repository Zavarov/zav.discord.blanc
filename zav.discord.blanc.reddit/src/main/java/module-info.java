module zav.discord.blanc.reddit {
  requires static org.eclipse.jdt.annotation;
  
  requires java.sql;
  
  requires com.google.guice;
  requires org.apache.logging.log4j;
  requires net.dv8tion.jda;
  requires zav.discord.blanc.api;
  requires zav.discord.blanc.databind;
  requires zav.discord.blanc.db;
  requires zav.jrc.api;
  requires zav.jrc.databind;
  requires zav.jcr.listener;
  requires zav.jrc.client;
  requires java.desktop;
  requires java.inject;
  requires org.apache.commons.lang3;
  
  exports zav.discord.blanc.reddit;
  
  opens zav.discord.blanc.reddit to com.google.guice;
}