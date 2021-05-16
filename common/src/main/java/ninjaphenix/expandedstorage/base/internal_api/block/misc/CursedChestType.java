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
        if (this == CursedChestType.TOP) {
            return BOTTOM;
        } else if (this == CursedChestType.BOTTOM) {
            return TOP;
        } else if (this == CursedChestType.FRONT) {
            return BACK;
        } else if (this == CursedChestType.BACK) {
            return FRONT;
        } else if (this == CursedChestType.LEFT) {
            return RIGHT;
        } else if (this == CursedChestType.RIGHT) {
            return LEFT;
        }
        throw new IllegalStateException("SINGLE CursedChestType has no opposite");
    }
}
