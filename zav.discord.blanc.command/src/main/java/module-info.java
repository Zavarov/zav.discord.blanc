module zav.discord.blanc.command {
  requires static org.eclipse.jdt.annotation;
  
  requires com.google.guice;
  requires java.inject;
  requires org.apache.logging.log4j;
  requires org.apache.commons.lang3;
  requires zav.discord.blanc.api;
  requires zav.discord.blanc.databind;
  
  exports zav.discord.blanc.command;
  exports zav.discord.blanc.command.parser;
  exports zav.discord.blanc.command.resolver;
  
  opens zav.discord.blanc.command to com.google.guice;
  opens zav.discord.blanc.command.parser to com.google.guice;
  exports zav.discord.blanc.command.guice;
  opens zav.discord.blanc.command.guice to com.google.guice;
}