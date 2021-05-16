package ninjaphenix.expandedstorage.chest;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class MixinPlugin implements IMixinConfigPlugin {
    @Override
    public boolean shouldApplyMixin(final String TARGET_CLASS_PATH, final String MIXIN_CLASS_PATH) {
        if ("ninjaphenix.expandedstorage.chest.mixin.ToweletteSupport".equals(MIXIN_CLASS_PATH)) {
            return FabricLoader.getInstance().isModLoaded("towelette");
        }
        return true;
    }

    @Override
    public void onLoad(final String MIXIN_PACKAGE) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(final Set<String> MY_CONFIG_TARGETS, final Set<String> OTHERS_CONFIG_TARGETS) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(final String TARGET_CLASS_PATH, final ClassNode TARGET_CLASS, final String MIXIN_CLASS_PATH, final IMixinInfo MIXIN_INFO) {

    }

    @Override
    public void postApply(final String TARGET_CLASS_PATH, final ClassNode TARGET_CLASS, final String MIXIN_CLASS_PATH, final IMixinInfo MIXIN_INFO) {

    }
}