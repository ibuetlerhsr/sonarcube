package actors;

import java.util.Set;

import static java.util.Objects.requireNonNull;

public final class Messages {

    public static final class WatchNewChallenges {


        @Override
        public String toString() {
            return "WatchNewChallenges()";
        }
    }

    public static final class UnwatchNewChallenges {

        @Override
        public String toString() {
            return "UnwatchNewChallenges()";
        }
    }
}

