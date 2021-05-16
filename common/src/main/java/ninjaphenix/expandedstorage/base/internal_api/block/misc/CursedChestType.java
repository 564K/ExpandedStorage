package ninjaphenix.expandedstorage.base.internal_api.block.misc;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

@Experimental
@Internal
public enum CursedChestType implements StringRepresentable {
    TOP("top", -1),
    BOTTOM("bottom", -1),
    FRONT("front", 0),
    BACK("back", 2),
    LEFT("left", 1),
    RIGHT("right", 3),
    SINGLE("single", -1);

    private final String name;
    private final int offset;

    CursedChestType(String name, int offset) {
        this.name = name;
        this.offset = offset;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public int offset() {
        return this.offset;
    }

    public CursedChestType getOpposite() {
        switch (this) {
            case TOP:
                return BOTTOM;
            case BOTTOM:
                return TOP;
            case FRONT:
                return BACK;
            case BACK:
                return FRONT;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
        }
        throw new IllegalStateException("SINGLE CursedChestType has no opposite");
    }
}
