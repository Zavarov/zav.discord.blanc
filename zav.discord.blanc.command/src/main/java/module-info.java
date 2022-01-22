open module zav.discord.blanc.command {
  requires static org.eclipse.jdt.annotation;
  requires static org.jetbrains.annotations;
  
  requires com.google.guice;
  requires net.dv8tion.jda;
  requires org.apache.logging.log4j;
  requires org.apache.commons.lang3;
  requires zav.discord.blanc.api;
  requires zav.discord.blanc.databind;
  requires zav.discord.blanc.db;

  requires java.inject;
  requires java.sql;
  
  exports zav.discord.blanc.command;
  exports zav.discord.blanc.command.parser;
}