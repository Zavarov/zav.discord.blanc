open module zav.discord.blanc.db.test {
  requires static org.eclipse.jdt.annotation;
  
  requires java.sql;

  requires org.assertj.core;
  requires org.junit.jupiter.api;
  requires org.mockito;
  requires org.mockito.junit.jupiter;
  requires net.bytebuddy;
  requires net.bytebuddy.agent;
  requires net.dv8tion.jda;
  
  requires test.io;
  requires zav.discord.blanc.databind;
  requires zav.discord.blanc.db;
}