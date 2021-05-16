package ninjaphenix.expandedstorage.base.config;

import net.minecraft.resources.ResourceLocation;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

public final class LegacyFactory implements Converter<Map<String, Object>, ConfigV0> {
    public static final LegacyFactory INSTANCE = new LegacyFactory();

    private LegacyFactory() {

    }

    @Override
    public ConfigV0 fromSource(@Unmodifiable final Map<String, Object> SOURCE) {
        final Object CONTAINER_TYPE = SOURCE.get("preferred_container_type");
        final Object RESTRICTIVE_SCROLLING = SOURCE.get("restrictive_scrolling");
        if (CONTAINER_TYPE instanceof String && RESTRICTIVE_SCROLLING instanceof Boolean) {
            String temp = (String) CONTAINER_TYPE;
            if ("expandedstorage:paged".equals(temp)) {
                temp = Utils.PAGE_CONTAINER_TYPE.toString();
            } else if ("expandedstorage:scrollable".equals(temp)) {
                temp = Utils.SCROLL_CONTAINER_TYPE.toString();
            }
            return new ConfigV0(ResourceLocation.tryParse(temp), (Boolean) (RESTRICTIVE_SCROLLING));
        }
        return null;
    }

    @Override
    public Map<String, Object> toSource(final ConfigV0 TARGET) {
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