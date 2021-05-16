package ninjaphenix.expandedstorage.base.config;

import com.electronwill.nightconfig.core.Config;
import net.minecraft.resources.ResourceLocation;
import ninjaphenix.expandedstorage.base.internal_api.Utils;

public final class LegacyFactory implements Converter<Config, ConfigV0> {
    public static final LegacyFactory INSTANCE = new LegacyFactory();

    private LegacyFactory() {

    }

    @Override
    public ConfigV0 fromSource(final Config SOURCE) {
        if (SOURCE != null) {
            final Object CONTAINER_TYPE = SOURCE.get("client.preferred_container_type");
            final Object RESTRICTIVE_SCROLLING = SOURCE.get("client.restrictive_scrolling");
            if (CONTAINER_TYPE instanceof String && RESTRICTIVE_SCROLLING instanceof Boolean) {
                String temp = (String) CONTAINER_TYPE;
                if ("expandedstorage:paged".equals(temp)) {
                    temp = Utils.PAGE_CONTAINER_TYPE.toString();
                } else if ("expandedstorage:scrollable".equals(temp)) {
                    temp = Utils.SCROLL_CONTAINER_TYPE.toString();
                }
                return new ConfigV0(ResourceLocation.tryParse(temp), (Boolean) RESTRICTIVE_SCROLLING);
            }
        }
        return null;
    }

    @Override
    public Config toSource(final ConfigV0 TARGET) {
        throw new UnsupportedOperationException("Legacy configs cannot be saved.");
    }

    @Override
    public int getSourceVersion() {
        return -1;
    }

    @Override
    public int getTargetVersion() {
        return 0;
    }
}