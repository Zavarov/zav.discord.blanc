/*
 * Copyright (c) 2021 Zavarov.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package zav.discord.blanc.jda.api;

import static zav.discord.blanc.jda.internal.DatabaseUtils.aboutWebHook;
import static zav.discord.blanc.jda.internal.MessageUtils.forLink;

import club.minnced.discord.webhook.external.JDAWebhookClient;
import javax.inject.Inject;

import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Webhook;
import zav.discord.blanc.api.WebHook;
import zav.discord.blanc.databind.WebHookValueObject;
import zav.jrc.databind.LinkValueObject;
import zav.jrc.databind.SubredditValueObject;


/**
 * Implementation of a web-hook view, backed by JDA.
 */
public class JdaWebHook implements WebHook {
  @Inject
  private Webhook jdaWebhook;
  
  @Inject
  private JDAWebhookClient jdaWebhookClient;
  
  @Override
  public WebHookValueObject getAbout() {
    return aboutWebHook(jdaWebhook);
  }
  
  @Override
  public void delete() {
    jdaWebhook.delete().complete();
  }
  
  @Override
  public void send(SubredditValueObject subreddit, LinkValueObject link) {
    // See https://github.com/MinnDevelopment/discord-webhooks/issues/53
    // Once that is fixed, replace with jdaWebhookClient(forLink(link))
    WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();

    Message message = forLink(link);
    
    for (MessageEmbed messageEmbed : message.getEmbeds()) {
      messageBuilder.addEmbeds(WebhookEmbedBuilder.fromJDA(messageEmbed).build());
    }
    
    messageBuilder.setAvatarUrl(subreddit.getIconImage());
    messageBuilder.setUsername("r/" + subreddit.getDisplayName());
  
    WebhookMessage webhookMessage = messageBuilder.build();
    
    jdaWebhookClient.send(webhookMessage);
  }
}
