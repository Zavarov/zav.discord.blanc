open module zav.discord.blanc.api.test {
  
  requires com.google.common;
  requires com.google.guice;
  requires net.bytebuddy;
  requires net.bytebuddy.agent;
  requires net.dv8tion.jda;
  requires org.apache.commons.lang3;
  requires org.assertj.core;
  requires org.mockito;
  requires org.mockito.junit.jupiter;
  requires org.junit.jupiter.api;
  requires org.junit.jupiter.params;

  requires java.sql;
  
  requires zav.discord.blanc.api;
  requires zav.discord.blanc.databind;
  requires zav.discord.blanc.db;
}