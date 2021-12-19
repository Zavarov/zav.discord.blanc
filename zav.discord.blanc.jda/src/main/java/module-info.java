module zav.discord.blanc.jda {
  requires static org.eclipse.jdt.annotation;
  
  requires java.desktop;
  requires net.dv8tion.jda;
  requires zav.discord.blanc.activity;
  requires zav.discord.blanc.api;
  requires zav.discord.blanc.databind;
  requires java.inject;
  requires zav.jrc.databind;
  requires org.apache.logging.log4j;
  requires discord.webhooks;
  requires zav.discord.blanc.command;
  requires com.google.common;
  requires com.google.guice;
  requires zav.discord.blanc.db;
  requires java.sql;
  
  exports zav.discord.blanc.jda;
  exports zav.discord.blanc.jda.api;
  
  opens zav.discord.blanc.jda.api to com.google.guice;
}