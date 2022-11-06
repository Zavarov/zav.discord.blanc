
package zav.discord.blanc.databind;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "botName",
    "globalPrefix",
    "shardCount",
    "inviteSupportServer",
    "wikiUrl",
    "discordToken",
    "redditId",
    "redditAccount",
    "redditSecret",
    "owner"
})
@Setter
@Getter
@NoArgsConstructor
public class Credentials {

  @JsonProperty("botName")
  private String botName;

  @JsonProperty("globalPrefix")
  private String globalPrefix;

  @JsonProperty("shardCount")
  private long shardCount;

  @JsonProperty("inviteSupportServer")
  private String inviteSupportServer;

  @JsonProperty("wikiUrl")
  private String wikiUrl;

  @JsonProperty("discordToken")
  private String discordToken;

  @JsonProperty("redditId")
  private String redditId;

  @JsonProperty("redditAccount")
  private String redditAccount;

  @JsonProperty("redditSecret")
  private String redditSecret;

  @JsonProperty("owner")
  private long owner;
}
