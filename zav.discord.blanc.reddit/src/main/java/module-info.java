open module zav.discord.blanc.reddit {
  requires static org.eclipse.jdt.annotation;
  requires static zav.jrc.http;
  
  requires com.google.guice;
  requires org.apache.commons.lang3;
  requires org.slf4j;
  requires net.dv8tion.jda;
  requires zav.discord.blanc.api;
  requires zav.discord.blanc.databind;
  requires zav.discord.blanc.db;
  requires zav.jrc.api;
  requires zav.jrc.databind;
  requires zav.jcr.listener;
  requires zav.jrc.client;
  
  requires transitive java.desktop;
  
  exports zav.discord.blanc.reddit;
}