package zav.discord.blanc.databind;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * This class correspond to all custom status messages of this bot.
 */
@Getter
@NoArgsConstructor
public class Status {
  private List<String> messages;

  /**
   * Creates a new instance of this object. The status messages are stored in a are read from the
   * file with name {@code fileName}, located in the root of the class path.
   *
   * @param classLoader The class loader used to load the status file.
   * @param fileName The file name containing the status messages.
   * @return A new instance of this class.
   * @throws IOException In case the file couldn't be read.
   */
  public static Status read(ClassLoader classLoader, String fileName) throws IOException {
    ObjectMapper om = new ObjectMapper();
    InputStream is = classLoader.getResourceAsStream(fileName);
    String[] messages = om.readValue(is, String[].class);

    Status result = new Status();
    result.messages = List.of(messages);
    return result;
  }
}
