package ninjaphenix.expandedstorage.base.internal_api.item;

import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Experimental
@Internal
public enum MutationMode {
    MERGE("merge", 0),
    SPLIT("split", 1),
    ROTATE("rotate", 2);

    private static final MutationMode[] VALUES = MutationMode.values();
    private static final Map<String, MutationMode> MODES_BY_NAME = Arrays
            .stream(VALUES)
            .collect(Collectors.toMap(MutationMode::toString, Function.identity()));
    private final String NAME;
    private final int INDEX;

    MutationMode(final String NAME, final int INDEX) {
        this.NAME = NAME;
        this.INDEX = INDEX;
    }

    public static MutationMode from(final String NAME) {
        return MODES_BY_NAME.get(NAME);
    }

    /**
     * @deprecated Should only be used for loading legacy storage mutators.
     */
    @Deprecated
    public static MutationMode from(final byte INDEX) {
        if (INDEX > -1 && INDEX < VALUES.length) {
            return VALUES[INDEX];
        }
        return null;
    }

    @Override
    public String toString() {
        return NAME;
    }

    public MutationMode next() {
        return VALUES[(INDEX + 1) % VALUES.length];
    }
}
