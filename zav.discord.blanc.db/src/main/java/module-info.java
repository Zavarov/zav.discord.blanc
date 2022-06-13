open module zav.discord.blanc.db {
  requires static org.eclipse.jdt.annotation;
  requires static org.jetbrains.annotations;
  
  requires java.sql;
  requires java.inject;
  
  requires org.apache.commons.lang3;
  requires com.fasterxml.jackson.databind;
  requires net.dv8tion.jda;
  requires org.slf4j;

  requires zav.discord.blanc.databind;
  
  exports zav.discord.blanc.db;
  exports zav.discord.blanc.db.sql;
}