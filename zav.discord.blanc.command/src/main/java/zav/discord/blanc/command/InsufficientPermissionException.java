package zav.discord.blanc.command;

import net.dv8tion.jda.api.Permission;
import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * This exception is thrown whenever a user executes a guild command for which they lack the
 * required permission.
 *
 * @see Permission
 */
@NonNullByDefault
public class InsufficientPermissionException extends Exception {
}
