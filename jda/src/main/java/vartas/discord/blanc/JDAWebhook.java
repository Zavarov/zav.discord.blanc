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

package vartas.discord.blanc;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import vartas.discord.blanc.$factory.WebhookFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class JDAWebhook extends Webhook{
    @Nonnull
    public static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(
            0,
            new ThreadFactoryBuilder().setNameFormat("Webhook#%d").build()
    );
    @Nonnull
    private final net.dv8tion.jda.api.entities.Webhook jdaWebhook;
    @Nonnull
    private final WebhookClient webhookClient;

    @Nonnull
    private JDAWebhook(@Nonnull net.dv8tion.jda.api.entities.Webhook jdaWebhook){
        this.jdaWebhook = jdaWebhook;
        //Avoid using a executor for each webhook instance to reduce performance issues.
        this.webhookClient = new WebhookClientBuilder(jdaWebhook.getUrl())
                .setExecutorService(EXECUTOR_SERVICE)
                .build();
    }

    public static Webhook create(@Nonnull net.dv8tion.jda.api.entities.Webhook webhook){
        return WebhookFactory.create(
                () -> new JDAWebhook(webhook),
                webhook.getIdLong(),
                webhook.getName()
        );
    }

    @Override
    public void send(Message message) {
        webhookClient.send(WebhookMessageBuilder.buildMessage(jdaWebhook.getJDA().getSelfUser(), message));
    }

    @Override
    public void send(byte[] bytes, String qualifiedName) {
        webhookClient.send(bytes, qualifiedName);
    }

    @Override
    public void shutdown(){
        EXECUTOR_SERVICE.shutdown();
    }
}
