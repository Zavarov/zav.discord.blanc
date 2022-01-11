module zav.discord.blanc.api {
  requires static org.eclipse.jdt.annotation;
  
  requires com.google.common;
  requires com.google.guice;
  requires net.dv8tion.jda;
  requires org.apache.commons.lang3;
  requires org.apache.logging.log4j;
  
  requires java.inject;
  
  exports zav.discord.blanc.api;
  exports zav.discord.blanc.api.command;
  exports zav.discord.blanc.api.command.parser;
}