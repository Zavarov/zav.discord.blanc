module zav.discord.blanc.databind {
  requires java.compiler;
  requires com.fasterxml.jackson.annotation;
  requires static org.eclipse.jdt.annotation;
  
  exports zav.discord.blanc.databind.activity;
  exports zav.discord.blanc.databind.message;
  exports zav.discord.blanc.databind;
}