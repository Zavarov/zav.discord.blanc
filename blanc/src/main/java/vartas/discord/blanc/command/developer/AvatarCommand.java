/*
 * Copyright (c) 2019 Zavarov
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

package vartas.discord.blanc.command.developer;

import net.dv8tion.jda.core.entities.Icon;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Message.Attachment;
import vartas.discord.bot.api.communicator.CommunicatorInterface;
import vartas.discord.bot.command.entity._ast.ASTEntityType;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This command changes the avatar of the bot to the one that was attached to
 * the message that executed this command.
 */
public class AvatarCommand extends AvatarCommandTOP{
    public AvatarCommand(Message source, CommunicatorInterface communicator, List<ASTEntityType> parameters) throws IllegalArgumentException, IllegalStateException {
        super(source, communicator, parameters);
    }

    /**
     * If the attachment is an image, it is used as the new avatar.
     */
    @Override
    public void run(){
        Attachment attachment = source.getAttachments().get(0);
        if(attachment.isImage()){
            try {
                String filename = attachment.getFileName();
                File file = File.createTempFile(filename, ".tmp");
                //In case we get THE SAME temporary file we got before
                file.delete();
                attachment.download(file);
                communicator.send(communicator.jda().getSelfUser().getManager().setAvatar(Icon.from(file)));
                file.delete();
                communicator.send(channel, "Avatar updated.");
            }catch(IOException e){
                communicator.send(channel, e.getMessage());
            }
        }else{
            communicator.send(channel,"Please make sure that you've attached an image.");
        }
    }
}
