package ninjaphenix.expandedstorage.base.config;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class ResourceLocationTypeAdapter extends TypeAdapter<ResourceLocation> {
    @Override
    public void write(JsonWriter out, ResourceLocation value) throws IOException {
        out.value(value.toString());
    }

    @Override // never used.
    public ResourceLocation read(JsonReader in) {
        return null;
    }
}
