package ninjaphenix.expandedstorage.base.config.button;

import java.util.Set;

public class ButtonOffset {
    private final Set<String> mods;
    private final int offset;

    public ButtonOffset(Set<String> mods, int offset) {
        this.mods = mods;
        this.offset = offset;
    }

    public boolean areModsPresent(Set<String> loadedMods) {
        return loadedMods.containsAll(mods);
    }

    public int getOffset() {
        return this.offset;
    }
}
