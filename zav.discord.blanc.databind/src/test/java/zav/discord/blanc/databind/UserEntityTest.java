package zav.discord.blanc.databind;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserEntityTest {
  UserEntity userEntity;
  
  @Mock User user;
  
  @BeforeEach
  public void setUp() {
    userEntity = UserEntity.find(user);
    userEntity.merge();
  }
  
  @AfterEach
  public void tearDown() {
    UserEntity.remove(user);
  }
  
  @Test
  public void testFindUser() {
    assertEquals(UserEntity.find(user).getId(), userEntity.getId());
  }
  
  @Test
  public void testFindUnknownUser() {
    when(user.getIdLong()).thenReturn(Long.MAX_VALUE);
    
    assertNotEquals(UserEntity.find(user).getId(), userEntity.getId());
  }
}
