module zav.discord.blanc.jda {
  requires static org.eclipse.jdt.annotation;
  
  requires com.google.common;
  requires com.google.guice;
  requires discord.webhooks;
  requires java.desktop;
  requires java.inject;
  requires java.sql;
  requires net.dv8tion.jda;
  requires org.apache.commons.lang3;
  requires org.apache.logging.log4j;
  requires zav.discord.blanc.activity;
  requires zav.discord.blanc.api;
  requires zav.discord.blanc.databind;
  requires zav.jrc.databind;
  requires zav.discord.blanc.command;
  requires zav.discord.blanc.db;
  
  exports zav.discord.blanc.jda;
  exports zav.discord.blanc.jda.api;
  
  opens zav.discord.blanc.jda.api to com.google.guice;
}