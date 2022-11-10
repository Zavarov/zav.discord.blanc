
package zav.discord.blanc.databind;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "shardCount",
    "inviteSupportServer",
    "token",
    "owner"
})
@Setter
@Getter
@NoArgsConstructor
public class Credentials {

  @JsonProperty("name")
  private String name;

  @JsonProperty("shardCount")
  private long shardCount;

  @JsonProperty("inviteSupportServer")
  private String inviteSupportServer;

  @JsonProperty("token")
  private String token;

  @JsonProperty("owner")
  private long owner;
  
  /**
   * Reads a JSON file and transforms it into a Java entity.<br>
   * The file path is relative to the the root directory.
   *
   * @param filePath The name of the JSON file.
   * @return An instance of the target file.
   * @throws IOException If the file couldn't be read.
   */
  public static Credentials read(String filePath) throws IOException {
    ObjectMapper om = new ObjectMapper();
    File file = new File(filePath);
    return om.readValue(file, Credentials.class);
  }
}
