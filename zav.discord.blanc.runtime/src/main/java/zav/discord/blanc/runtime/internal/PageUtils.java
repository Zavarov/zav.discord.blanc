package zav.discord.blanc.runtime.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import zav.discord.blanc.api.Site;

/**
 * Utility class for creating a rich page from a list of data.
 */
public class PageUtils {
  /**
   * Transforms the given {@code data} into a list of pages. Each page contains at most
   * {code itemsPerPage} elements.
   *
   * @param label The label for each page.
   * @param data The data that is displayed over all pages.
   * @param itemsPerPage The number of items per page.
   * @return A list of all created pages.
   */
  public static List<Site.Page> convert(String label, List<String> data, int itemsPerPage) {
    List<Site.Page> result = new ArrayList<>();
    
    for (int i = 0; i < data.size(); i += itemsPerPage) {
      List<MessageEmbed> messages = new ArrayList<>();
      EmbedBuilder builder = new EmbedBuilder();
      
      for (int j = 0; j < Math.min(data.size(), itemsPerPage); ++j) {
        String name = Integer.toString(j);
        String value = MarkdownSanitizer.escape(data.get(i +  j));
        builder.addField(name, value, false);
      }
      
      messages.add(builder.build());
      result.add(Site.Page.create(label, messages));
    }
    
    return Collections.unmodifiableList(result);
  }
}
