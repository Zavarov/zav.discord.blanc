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

package vartas.discord.blanc.command.reddit;

import com.google.common.collect.*;
import vartas.reddit.Comment;
import vartas.reddit.Submission;
import vartas.reddit.SubmissionTOP;
import vartas.reddit.Subreddit;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface SnowflakeCommand {
    DiscreteDomain<Instant> domain = new InstantDomain();

    default long countNsfwSubmissions(@Nonnull Subreddit subreddit, @Nonnull Range<Instant> range){
        return ContiguousSet.create(range, domain)
                .parallelStream()
                .map(date -> subreddit.getUncheckedSubmissions(date, domain.next(date)))
                .flatMap(Collection::stream)
                .filter(Submission::getNsfw)
                .count();
    }

    default long countSpoilerSubmissions(@Nonnull Subreddit subreddit, @Nonnull Range<Instant> range){
        return ContiguousSet.create(range, domain)
                .parallelStream()
                .map(date -> subreddit.getUncheckedSubmissions(date, domain.next(date)))
                .flatMap(Collection::stream)
                .filter(Submission::getSpoiler)
                .count();
    }

    default long countSubmissions(@Nonnull Subreddit subreddit, @Nonnull Range<Instant> range){
        return ContiguousSet.create(range, domain)
                .parallelStream()
                .map(date -> subreddit.getUncheckedSubmissions(date, domain.next(date)))
                .mapToLong(List::size)
                .sum();
    }

    default long countComments(@Nonnull Subreddit subreddit, @Nonnull Range<Instant> range){
        return ContiguousSet.create(range, domain)
                .parallelStream()
                .map(date -> subreddit.getUncheckedSubmissions(date, domain.next(date)))
                .flatMap(Collection::stream)
                .map(Submission::getComments)
                .mapToLong(List::size)
                .sum();
    }

    default long countUniqueSubmitters(@Nonnull Subreddit subreddit, @Nonnull Range<Instant> range){
        return ContiguousSet.create(range, domain)
                .parallelStream()
                .map(date -> subreddit.getUncheckedSubmissions(date, domain.next(date)))
                .flatMap(Collection::stream)
                .map(Submission::getAuthor)
                .distinct()
                .count();
    }

    default long countUniqueCommenters(@Nonnull Subreddit subreddit, @Nonnull Range<Instant> range){
        return ContiguousSet.create(range, domain)
                .parallelStream()
                .map(date -> subreddit.getUncheckedSubmissions(date, domain.next(date)))
                .flatMap(Collection::stream)
                .map(Submission::getComments)
                .flatMap(Collection::stream)
                .map(Comment::getAuthor)
                .distinct()
                .count();
    }

    default long countTotalSubmissionScore(@Nonnull Subreddit subreddit, @Nonnull Range<Instant> range){
        return ContiguousSet.create(range, domain)
                .parallelStream()
                .map(date -> subreddit.getUncheckedSubmissions(date, domain.next(date)))
                .flatMap(Collection::stream)
                .mapToLong(Submission::getScore)
                .sum();
    }

    default long countTotalCommentScore(@Nonnull Subreddit subreddit, @Nonnull Range<Instant> range){
        return ContiguousSet.create(range, domain)
                .parallelStream()
                .map(date -> subreddit.getUncheckedSubmissions(date, domain.next(date)))
                .flatMap(Collection::stream)
                .map(Submission::getComments)
                .flatMap(Collection::stream)
                .mapToLong(Comment::getScore)
                .sum();
    }

    default double countSubmissionsPerDay(@Nonnull Subreddit subreddit, @Nonnull Range<Instant> range){
        double numerator = countSubmissions(subreddit, range);
        double denominator = domain.distance(range.lowerEndpoint(), range.upperEndpoint());

        return numerator / denominator;
    }

    default double countCommentsPerDay(@Nonnull Subreddit subreddit, @Nonnull Range<Instant> range){
        double numerator = countComments(subreddit, range);
        double denominator = domain.distance(range.lowerEndpoint(), range.upperEndpoint());

        return numerator / denominator;
    }

    default Set<Submission> getTopSubmissions(@Nonnull Subreddit subreddit, @Nonnull Range<Instant> range, int size){
        return ContiguousSet.create(range, domain)
                .parallelStream()
                .map(date -> subreddit.getUncheckedSubmissions(date, domain.next(date)))
                .flatMap(Collection::stream)
                .filter(submission -> !submission.getAuthor().equals("[deleted]"))
                .collect(Collectors.toCollection(() -> new LimitedSubmissionSet(size)));
    }

    default Set<Comment> getTopComments(@Nonnull Subreddit subreddit, @Nonnull Range<Instant> range, int size){
        return ContiguousSet.create(range, domain)
                .parallelStream()
                .map(date -> subreddit.getUncheckedSubmissions(date, domain.next(date)))
                .flatMap(Collection::stream)
                .map(Submission::getComments)
                .flatMap(Collection::stream)
                .filter(submission -> !submission.getAuthor().equals("[deleted]"))
                .collect(Collectors.toCollection(() -> new LimitedCommentSet(size)));
    }

    default SortedSet<Map.Entry<String, LimitedSubmissionSet>> getTopSubmitters(@Nonnull Subreddit subreddit, @Nonnull Range<Instant> range, int size){
        return ContiguousSet.create(range, domain)
                .parallelStream()
                .map(date -> subreddit.getUncheckedSubmissions(date, domain.next(date)))
                .flatMap(Collection::stream)
                .filter(e -> !e.getAuthor().equals("[deleted]"))
                .collect(Collectors.groupingBy(Submission::getAuthor, Collectors.toCollection(() -> new LimitedSubmissionSet(size))))
                .entrySet()
                .stream()
                .limit(size)
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingLong(u -> u.getValue().getScore()))));
    }

    default SortedSet<Map.Entry<String, LimitedCommentSet>> getTopCommenters(@Nonnull Subreddit subreddit, @Nonnull Range<Instant> range, int size){
        return ContiguousSet.create(range, domain)
                .parallelStream()
                .map(date -> subreddit.getUncheckedSubmissions(date, domain.next(date)))
                .flatMap(Collection::stream)
                .map(Submission::getComments)
                .flatMap(Collection::stream)
                .filter(e -> !e.getAuthor().equals("[deleted]"))
                .collect(Collectors.groupingBy(Comment::getAuthor, Collectors.toCollection(() -> new LimitedCommentSet(size))))
                .entrySet()
                .stream()
                .limit(size)
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingLong(u -> u.getValue().getScore()))));
    }

    //------------------------------------------------------------------------------------------------------------------
    //
    //      Utility Classes
    //
    //------------------------------------------------------------------------------------------------------------------

    class LimitedSubmissionSet extends LimitedSnowflakeSet<Submission> {
        public LimitedSubmissionSet(int size) {
            super(size);
        }

        @Override
        protected long getScore(Submission object) {
            return object.getScore();
        }
    }

    class LimitedCommentSet extends LimitedSnowflakeSet<Comment> {
        public LimitedCommentSet(int size) {
            super(size);
        }

        @Override
        protected long getScore(Comment object) {
            return object.getScore();
        }
    }

    /**
     * A helper class to minimize the overhead when grouping entities.
     * In case where we have to group snowflakes based on an attribute, to get the ones with the highest score,
     * it is not required to keep those entities, that are outside of the range we are interested in.
     * However, if we would simply discard them, we would run into troubles when sorting them based on their
     * combined score.<br>
     * To solve this issue, we separate the top entities from the accumulated score.
     * @param <T> the entity type.
     */
    abstract class LimitedSnowflakeSet<T> extends AbstractSet<T> {
        /**
         * The internal sorted set, containing the top snowflakes.
         */
        private final TreeSet<T> delegator;
        /**
         * The number of top snowflakes that are kept.
         */
        private final int size;
        /**
         * The combined score over all snowflakes.
         */
        private long score = 0;
        /**
         * The number of snowflakes added to the set.
         */
        private long count;


        /**
         * Creates an empty set.
         * @param size the number of top snowflakes that are kept.
         */
        public LimitedSnowflakeSet(int size){
            Comparator<T> comparator = Comparator.comparingLong(this::getScore);

            this.size = size;
            this.delegator = new TreeSet<>(comparator.reversed());
        }

        /**
         * @return the combined score of all snowflakes.
         */
        public long getScore(){
            return score;
        }

        protected abstract long getScore(T object);

        /**
         * @return the total amount of snowflakes.
         */
        public long getCount(){
            return count;
        }

        /**
         * @return a {@link Stream} over all top snowflakes.
         */
        public Stream<T> getValues(){
            return delegator.stream();
        }

        /**
         * If the score is higher than the bottom of the internal set, the bottom is removed and the new entry
         * is added to the set. Otherwise the new element is discarded.<br>
         * In any case, the score of the new snowflake is added to the accumulated score.
         * @param object the object that is added to the set
         * @return true
         */
        @Override
        public synchronized boolean add(T object){
            delegator.add(object);
            score += getScore(object);
            count++;
            if(delegator.size() > size){
                //Delete the element with the lowest score
                Iterator<T> iterator = delegator.descendingIterator();
                iterator.next();
                iterator.remove();
            }

            return true;
        }

        /**
         * @return an iterator over the top snowflakes
         */
        @Nonnull
        @Override
        public Iterator<T> iterator() {
            return delegator.iterator();
        }

        /**
         * @return the number of top submissions stored so far
         */
        @Override
        public int size() {
            return delegator.size();
        }
    }

    class InstantDomain extends DiscreteDomain<Instant> {
        @Override
        public Instant minValue(){
            return Instant.MIN;
        }

        @Override
        public Instant maxValue(){
            return Instant.MAX;
        }

        @Override
        public Instant next(@Nonnull Instant value) {
            return value.plus(1, ChronoUnit.DAYS);
        }

        @Override
        public Instant previous(@Nonnull Instant value) {
            return value.minus(1, ChronoUnit.DAYS);
        }

        @Override
        public long distance(@Nonnull Instant start, @Nonnull Instant end) {
            return ChronoUnit.DAYS.between(start, end);
        }
    }
}
