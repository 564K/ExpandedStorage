package ninjaphenix.expandedstorage.base.config.button;

import ninjaphenix.expandedstorage.base.internal_api.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ButtonOffsetConfig {
    private final ButtonOffset[] fabric;
    private final ButtonOffset[] forge;

    public ButtonOffsetConfig(ButtonOffset[] fabric, ButtonOffset[] forge) {
        this.fabric = fabric;
        this.forge = forge;
    }

    private static ButtonOffsetConfig getDefaultConfig() {
        return new ButtonOffsetConfig(new ButtonOffset[]{}, new ButtonOffset[]{});
    }

    public static ButtonOffsetConfig loadButtonOffsetConfig(Path configPath) {
        Path dir = configPath.getParent();
        if (Files.notExists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                System.err.println("Failed to create directories needed for page button config, using default.");
                e.printStackTrace();
                return ButtonOffsetConfig.getDefaultConfig();
            }
        }
        if (Files.exists(configPath)) {
            try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                return Utils.GSON.fromJson(reader, ButtonOffsetConfig.class);
            } catch (IOException e) {
                System.err.println("Failed to read page button config, using default.");
                e.printStackTrace();
                return ButtonOffsetConfig.getDefaultConfig();
            }
        } else {
            ButtonOffsetConfig defaultConfig = ButtonOffsetConfig.getDefaultConfig();
            try (BufferedWriter writer = Files.newBufferedWriter(configPath, StandardOpenOption.CREATE)) {
                Utils.GSON.toJson(defaultConfig, ButtonOffsetConfig.class, Utils.GSON.newJsonWriter(writer));
            } catch (IOException e) {
                System.err.println("Failed to save default page button config.");
                e.printStackTrace();
            }
            return defaultConfig;
        }
    }

    public ButtonOffset[] getFabricConfigs() {
        return fabric;
    }

    public ButtonOffset[] getForgeConfigs() {
        return forge;
    }
}
