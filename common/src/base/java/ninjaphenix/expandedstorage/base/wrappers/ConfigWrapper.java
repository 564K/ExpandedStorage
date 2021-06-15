package ninjaphenix.expandedstorage.base.wrappers;

import net.minecraft.resources.ResourceLocation;

public interface ConfigWrapper {
    static ConfigWrapper getInstance() {
        return ConfigWrapperImpl.getInstance();
    }

    void initialise();

    boolean isScrollingUnrestricted();

    void setScrollingRestricted(boolean value);

    ResourceLocation getPreferredContainerType();

    boolean setPreferredContainerType(ResourceLocation containerType);
}
