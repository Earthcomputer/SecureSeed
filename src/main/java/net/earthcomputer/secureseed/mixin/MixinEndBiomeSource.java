package net.earthcomputer.secureseed.mixin;

import net.earthcomputer.secureseed.Globals;
import net.earthcomputer.secureseed.IChunkRandom;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TheEndBiomeSource.class)
public class MixinEndBiomeSource {
    @ModifyVariable(method = "<init>(Lnet/minecraft/util/registry/Registry;JLnet/minecraft/world/biome/Biome;Lnet/minecraft/world/biome/Biome;Lnet/minecraft/world/biome/Biome;Lnet/minecraft/world/biome/Biome;Lnet/minecraft/world/biome/Biome;)V",
            at = @At(value = "STORE", ordinal = 0),
            ordinal = 0)
    private ChunkRandom modifyChunkRandom(ChunkRandom rand) {
        ((IChunkRandom) rand).secureseed_setSeed(Globals.worldSeed, 0, 0, Globals.dimension.get(), Globals.END_BIOME_NOISE_SALT, 0);
        return rand;
    }
}
