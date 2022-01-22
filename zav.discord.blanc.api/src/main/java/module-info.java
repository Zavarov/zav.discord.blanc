open module zav.discord.blanc.api {
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
}