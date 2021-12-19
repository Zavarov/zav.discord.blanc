module zav.discord.blanc.activity {
  requires static org.eclipse.jdt.annotation;

  requires core;
  requires jfreechart;

  requires com.google.common;
  requires org.apache.logging.log4j;
  requires java.inject;
  requires java.desktop;
  requires zav.discord.blanc.api;
  requires zav.discord.blanc.databind;
  
  exports zav.discord.blanc.activity;
}