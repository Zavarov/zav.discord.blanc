package zav.discord.blanc.databind;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Status {
  private List<String> messages;

  public static Status read(ClassLoader classLoader, String fileName) throws IOException {
    ObjectMapper om = new ObjectMapper();
    InputStream is = classLoader.getResourceAsStream("Status.json");
    String[] messages = om.readValue(is, String[].class);

    Status result = new Status();
    result.messages = List.of(messages);
    return result;
  }
}
