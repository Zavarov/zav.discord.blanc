@SuppressWarnings("Java9RedundantRequiresStatement")
module zav.discord.blanc.api {
  requires static org.eclipse.jdt.annotation;
  requires static org.jetbrains.annotations;
  
  requires com.google.common;
  requires com.google.guice;
  requires net.dv8tion.jda;
  requires org.apache.commons.lang3;
  requires org.apache.logging.log4j;
  requires zav.discord.blanc.databind;
  requires zav.discord.blanc.db;
  
  requires java.inject;
  
  exports zav.discord.blanc.api;
  exports zav.discord.blanc.api.command;
  exports zav.discord.blanc.api.command.parser;
  
  opens zav.discord.blanc.api to com.google.guice;
  opens zav.discord.blanc.api.internal to com.google.guice;
  opens zav.discord.blanc.api.internal.listener to com.google.guice;
}