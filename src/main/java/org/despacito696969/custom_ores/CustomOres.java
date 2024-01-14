package org.despacito696969.custom_ores;

import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Path;

public class CustomOres implements ModInitializer {
    public static final String mod_id = "custom_ores";
    public static final Logger LOGGER = LoggerFactory.getLogger(mod_id);
    public static final String WRONG_TYPE_ERROR = "Config must contain an array of {name: <string>, biome_tag: <string>}";
    public static final String CONFIG_FILE_NAME = "custom_ores.json";

    @Override
    public void onInitialize() {
        loadOres();
    }

    public void loadOres() {
        Path config_path = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME);
        File config_file = config_path.toFile();
        Reader reader = null;
        try {
            reader = Files.newReader(config_file, Charset.defaultCharset());
        }
        catch (FileNotFoundException e) {
            LOGGER.error("No config detected! (.minecraft/config/" + CONFIG_FILE_NAME + ")");
            return;
        }

        JsonElement element;
        try {
            element = JsonParser.parseReader(reader);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            return;
        }

        if (!element.isJsonArray()) {
            LOGGER.error(WRONG_TYPE_ERROR);
            return;
        }
        JsonArray ores = element.getAsJsonArray();

        for (var ore: ores.asList()) {
            process_ore(ore);
        }
    }

    @Nullable
    static private String getString(@Nullable JsonElement elem) {
        if (elem == null) {
            return null;
        }
        if (!elem.isJsonPrimitive()) {
            return null;
        }
        var prim = elem.getAsJsonPrimitive();
        return prim.isString() ? prim.getAsString() : null;
    }

    private void process_ore(JsonElement ore) {
        if (!ore.isJsonObject()) {
            LOGGER.error(WRONG_TYPE_ERROR);
            return;
        }
        var obj = ore.getAsJsonObject();

        boolean abort = false;
        String name = getString(obj.get("name"));
        if (name == null) {
            LOGGER.error("No name in " + obj);
            abort = true;
        }

        String biome_tag = getString(obj.get("biome_tag"));
        if (biome_tag == null) {
            LOGGER.error("No biome_tag in " + obj);
            abort = true;
        }

        if (abort) {
            return;
        }

        var biome_tag_loc = new ResourceLocation(biome_tag);
        var feature_loc = new ResourceLocation(name);

        TagKey<Biome> tag = TagKey.create(Registries.BIOME, biome_tag_loc);
        var key = ResourceKey.create(Registries.PLACED_FEATURE, feature_loc);
        BiomeModifications.addFeature(BiomeSelectors.tag(tag), GenerationStep.Decoration.UNDERGROUND_ORES, key);
    }
}
