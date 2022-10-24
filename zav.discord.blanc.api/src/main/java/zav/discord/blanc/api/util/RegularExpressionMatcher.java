package zav.discord.blanc.api.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.databind.AutoResponseEntity;

/**
 * A regex-based pattern matcher. It joins all entries into a single regex, each entry uniquely
 * identified by a group name. For each group, a response is specified.<br>
 * If a string matches one or more groups, the response from the first matched group is returned.
 */
public class RegularExpressionMatcher {
  private static final Logger LOGGER = LoggerFactory.getLogger(RegularExpressionMatcher.class);
  private final Pattern pattern;
  private final List<String> groupNames;
  private final List<String> responses;
  
  /**
   * Creates a new matcher instance.
   *
   * @param patterns A list of automatic responses.
   */
  public RegularExpressionMatcher(List<AutoResponseEntity> patterns) {
    this.responses = createResponses(patterns);
    this.groupNames = createGroupNames(patterns);
    this.pattern = createPattern(patterns);
    
    LOGGER.debug("Created pattern {}", pattern.toString());
  }
  
  /**
   * Creates a list over all responses. The position of the response mirrors the position of the
   * pattern.
   *
   * @param patterns A list of automatic responses.
   * @return A list of all responses.
   * @see AutoResponseEntity#getAnswer()
   */
  private List<String> createResponses(List<AutoResponseEntity> patterns) {
    return patterns.stream()
        .map(AutoResponseEntity::getAnswer)
        .collect(Collectors.toUnmodifiableList());
  }
  
  /**
   * For the patterns {@code 0} to {@code n-1}, creates the group names {@code g0} to
   * {@code g(n-1)}.
   *
   * @param patterns A list of automatic responses.
   * @return A list of all group names.
   */
  private List<String> createGroupNames(List<AutoResponseEntity> patterns) {
    List<String> result = new ArrayList<>();
    
    for (int i = 0; i < patterns.size(); ++i) {
      result.add("g" + i);
    }
    
    return Collections.unmodifiableList(result);
  }
  
  /**
   * Creates the pattern containing all entries. Each entry is wrapped around a named group. All
   * entries are joined with an {@code or}.
   *
   * @param patterns A list of automatic responses.
   * @return The pattern matching any of the given entries.
   */
  private Pattern createPattern(List<AutoResponseEntity> patterns) {
    List<String> groups = new ArrayList<>(patterns.size());
    
    for (int i = 0; i < patterns.size(); ++i) {
      String groupName = groupNames.get(i);
      String pattern = patterns.get(i).getPattern();
      
      // Note: The pattern can't allow named groups!
      String group = MessageFormat.format("(?<{0}>{1})", groupName, pattern);
      groups.add(group);
    }
    
    return Pattern.compile(StringUtils.join(groups, "|"), Pattern.CASE_INSENSITIVE);
  }
  
  /**
   * Checks the input string for any pattern matches. Returns the first response if a match is
   * found, otherwise {@link Optional#empty()}.
   *
   * @param source An arbitrary string.
   * @return The automatic response of the first matching pattern.
   */
  public Optional<String> match(String source) {
    Matcher matcher = pattern.matcher(source);
    
    if (matcher.find()) {
      for (int i = 0; i < groupNames.size(); ++i) {
        // Find the first matching group and return its response
        if (matcher.group(groupNames.get(i)) != null) {
          return Optional.of(responses.get(i));
        }
      }
    }
    
    return Optional.empty();
  }
}
