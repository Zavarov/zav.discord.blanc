package zav.discord.blanc.runtime.internal.validator;

import java.util.function.Predicate;

/**
 * Classes implementing this interface provide the capability to check, whether an entity is still
 * up-to-date. Outdated entities are then automatically removed from the database.
 *
 * @param <T> The entity type this validator can accept.
 */
public interface Validator<T> extends Predicate<T> {

}
