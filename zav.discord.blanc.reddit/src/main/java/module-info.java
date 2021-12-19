module zav.discord.blanc.reddit {
  requires static org.eclipse.jdt.annotation;
  
  requires com.google.guice;
  requires org.apache.logging.log4j;
  requires zav.discord.blanc.api;
  requires zav.discord.blanc.databind;
  requires zav.jrc.api;
  requires zav.jrc.databind;
  requires zav.jcr.listener;
  requires zav.jcr.view;
  
  exports zav.discord.blanc.reddit;
}