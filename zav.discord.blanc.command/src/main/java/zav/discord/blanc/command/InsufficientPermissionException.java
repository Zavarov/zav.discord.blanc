package zav.discord.blanc.command;


import zav.discord.blanc.Permission;

/**
 * This exception is thrown whenever a user executes a guild command for which they lack the
 * required permission.
 *
 * @see Permission
 */
public class InsufficientPermissionException extends InvalidCommandException {
}
