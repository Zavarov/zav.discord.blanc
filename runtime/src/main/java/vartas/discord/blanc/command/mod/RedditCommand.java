/*
 * Copyright (c) 2020 Zavarov
 *
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
package vartas.discord.blanc.command.mod;


import vartas.discord.blanc.Shard;
import vartas.discord.blanc.TextChannel;
import vartas.discord.blanc.Webhook;

import java.util.Collection;

/**
 * This command allows to link subreddits to channels.
 */
public class RedditCommand extends RedditCommandTOP {
    private static final String WEBHOOK_NAME = "Reddit";

    @Override
    public void run(){
        TextChannel textChannel = getTextChannel().orElse(get$TextChannel());
        Collection<Webhook> webhooks = textChannel.retrieveWebhooks(WEBHOOK_NAME);

        //Cancel the feed if the bot can't post them in the targeted channel
        if(!get$Guild().canInteract(get$Guild().retrieveSelfMember(), textChannel)){
            get$TextChannel().send("I can't interact with "+textChannel.getName());
        //Attempt to remove the subreddit from all webhooks
        }else if(removeSubmissions(webhooks)){
            get$TextChannel().send("Submissions from r/" + subreddit + " will no longer be posted in " + textChannel.getAsMention() + ".");
        //Bind the submission to one of the webhooks, in case none were removed.
        }else{
            addSubmission(webhooks, textChannel);
            get$TextChannel().send("Submissions from r/"+subreddit+" will be posted in "+textChannel.getAsMention()+".");
        }
    }

    private boolean removeSubmissions(Collection<Webhook> webhooks){
        boolean modified = false;

        for(Webhook webhook : webhooks){
            if(webhook.removeSubreddits(getSubreddit())){
                modified = true;
                Shard.write(get$Guild(), webhook);
            }
        }

        return modified;
    }

    private void addSubmission(Collection<Webhook> webhooks, TextChannel textChannel){
        Webhook webhook = webhooks.stream().findAny().orElse(textChannel.createWebhook(WEBHOOK_NAME));
        webhook.addSubreddits(getSubreddit());
        Shard.write(get$Guild(), webhook);
    }
}
