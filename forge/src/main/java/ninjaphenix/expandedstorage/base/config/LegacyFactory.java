package ninjaphenix.expandedstorage.base.config;

import com.electronwill.nightconfig.core.Config;
import net.minecraft.resources.ResourceLocation;
import ninjaphenix.expandedstorage.base.internal_api.Utils;

public final class LegacyFactory implements Converter<Config, ConfigV0> {
    public static final LegacyFactory INSTANCE = new LegacyFactory();

    private LegacyFactory() {

    }

    @Override
    public ConfigV0 fromSource(Config source) {
        if (source != null) {
            Object containerType = source.get("client.preferred_container_type");
            Object restrictiveScrolling = source.get("client.restrictive_scrolling");
            if (containerType instanceof String && restrictiveScrolling instanceof Boolean) {
                String temp = (String) containerType;
                if ("expandedstorage:paged".equals(temp)) {
                    temp = Utils.PAGE_CONTAINER_TYPE.toString();
                } else if ("expandedstorage:scrollable".equals(temp)) {
                    temp = Utils.SCROLL_CONTAINER_TYPE.toString();
                }
                return new ConfigV0(ResourceLocation.tryParse(temp), (Boolean) restrictiveScrolling);
            }
        }
        return null;
    }

    @Override
    public Config toSource(ConfigV0 target) {
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
