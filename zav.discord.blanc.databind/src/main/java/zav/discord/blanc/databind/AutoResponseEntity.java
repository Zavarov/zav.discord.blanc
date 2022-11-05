package zav.discord.blanc.databind;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Generated
@NoArgsConstructor
@Entity
@Table(name = "AutoResponse")
public class AutoResponseEntity implements PersistedEntity {
  
  /**
   * The guild entity this object is contained by.
   */
  @ManyToOne
  private GuildEntity guild;
  
  /**
   * The expression all messages are matched against.
   */
  private String pattern;
  
  /**
   * The message that is returned on a match.
   */
  private String answer;
  
  /**
   * The internal entity id.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  
  public static AutoResponseEntity create(String regex, String answer) {
    AutoResponseEntity entity = new AutoResponseEntity();
    
    entity = new AutoResponseEntity();
    entity.setPattern(regex);
    entity.setAnswer(answer);
    
    return entity;
  }
}
