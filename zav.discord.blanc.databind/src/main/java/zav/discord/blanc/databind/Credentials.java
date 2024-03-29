
package zav.discord.blanc.databind;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * This PoJo represents the Discord credentials, together with additional properties such as the
 * name and shard count.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "shardCount",
    "inviteSupportServer",
    "token",
    "owner"
})
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
   * @param file The JSON file.
   * @return An instance of the target file.
   * @throws IOException If the file couldn't be read.
   */
  public static Credentials read(File file) throws IOException {
    return new ObjectMapper().readValue(file, Credentials.class);
  }
}
