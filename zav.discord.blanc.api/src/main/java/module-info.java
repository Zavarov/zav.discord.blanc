module zav.discord.blanc.api {
  requires java.desktop;
  requires zav.discord.blanc.databind;
  requires zav.jrc.databind;
  
  requires static org.eclipse.jdt.annotation;
  
  exports zav.discord.blanc.api;
  exports zav.discord.blanc.api.site;
}