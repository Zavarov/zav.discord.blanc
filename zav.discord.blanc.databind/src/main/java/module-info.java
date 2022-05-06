module zav.discord.blanc.databind {
  requires static org.eclipse.jdt.annotation;
  requires static org.jetbrains.annotations;
  
  requires com.fasterxml.jackson.annotation;
  
  requires java.compiler;
  
  exports zav.discord.blanc.databind.io;
  exports zav.discord.blanc.databind;
}