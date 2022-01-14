@SuppressWarnings("Java9RedundantRequiresStatement") // IntelliJ seems to ignore generated classes...
module zav.discord.blanc.databind {
  requires static org.eclipse.jdt.annotation;
  requires static org.jetbrains.annotations;
  
  requires transitive com.fasterxml.jackson.annotation;
  requires transitive com.fasterxml.jackson.databind;
  requires transitive java.compiler;
  
  exports zav.discord.blanc.databind.io;
  exports zav.discord.blanc.databind;
}