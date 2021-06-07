package ninjaphenix.expandedstorage.base.config.button;

import ninjaphenix.expandedstorage.base.internal_api.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;

public class ButtonOffsetConfig {
    private static ButtonOffset[] getDefaultConfig() {
        //noinspection OptionalGetWithoutIsPresent
        switch (Utils.getPlatform().get()) {
            case "fabric" -> {
                return new ButtonOffset[] {
                        new ButtonOffset(Collections.singleton("inventoryprofiles"), -12),
                        new ButtonOffset(Collections.singleton("inventorysorter"), -18)
                };
            }
            case "forge" -> {
                return new ButtonOffset[]{};
            }
        }
        throw new IllegalStateException();
    }

    public static ButtonOffset[] loadButtonOffsetConfig(Path configPath) {
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
                return Utils.GSON.fromJson(reader, ButtonOffset[].class);
            } catch (IOException e) {
                System.err.println("Failed to read page button config, using default.");
                e.printStackTrace();
                return ButtonOffsetConfig.getDefaultConfig();
            }
        } else {
            ButtonOffset[] defaultConfig = ButtonOffsetConfig.getDefaultConfig();
            try (BufferedWriter writer = Files.newBufferedWriter(configPath, StandardOpenOption.CREATE)) {
                Utils.GSON.toJson(defaultConfig, ButtonOffset[].class, Utils.GSON.newJsonWriter(writer));
            } catch (IOException e) {
                System.err.println("Failed to save default page button config.");
                e.printStackTrace();
            }
            return defaultConfig;
        }
    }
}
