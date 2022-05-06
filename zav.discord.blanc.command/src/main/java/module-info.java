open module zav.discord.blanc.command {
  requires static org.eclipse.jdt.annotation;
  requires static org.jetbrains.annotations;
  requires static zav.discord.blanc.databind;
  
  requires java.inject;
  requires net.dv8tion.jda;
  requires org.apache.commons.lang3;
  requires zav.discord.blanc.api;
  requires zav.discord.blanc.db;
  
  exports zav.discord.blanc.command;
}