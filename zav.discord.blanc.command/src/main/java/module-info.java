open module zav.discord.blanc.command {
  requires static com.github.spotbugs.annotations;
  requires static org.eclipse.jdt.annotation;
  requires static org.jetbrains.annotations;
  
  requires net.dv8tion.jda;
  requires org.apache.commons.lang3;
  
  requires java.inject;
  
  requires zav.discord.blanc.api;
  requires zav.discord.blanc.databind;
  requires zav.discord.blanc.db;
  
  exports zav.discord.blanc.command;
}