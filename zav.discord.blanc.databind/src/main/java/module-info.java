module zav.discord.blanc.databind {
  requires static org.eclipse.jdt.annotation;
  
  requires com.fasterxml.jackson.annotation;
  requires java.compiler;
  
  exports zav.discord.blanc.databind.io;
  exports zav.discord.blanc.databind.message;
  exports zav.discord.blanc.databind;
}