package zav.discord.blanc.command;

import net.dv8tion.jda.api.Permission;

/**
 * This exception is thrown whenever a user executes a guild command for which they lack the
 * required permission.
 *
 * @see Permission
 */
public class InsufficientPermissionException extends Exception {
}
