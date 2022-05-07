open module zav.discord.blanc.command.test {
  requires com.google.guice;
  requires net.bytebuddy;
  requires net.bytebuddy.agent;
  requires net.dv8tion.jda;
  requires org.assertj.core;
  requires org.junit.jupiter.api;
  requires org.mockito;
  requires org.mockito.junit.jupiter;
  
  requires java.sql;

  requires test.io;
  requires zav.discord.blanc.api;
  requires zav.discord.blanc.command;
  requires zav.discord.blanc.databind;
  requires zav.discord.blanc.db;
}