package net.earthcomputer.secureseed.mixin;

import net.earthcomputer.secureseed.Globals;
import net.earthcomputer.secureseed.IChunkRandom;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.surfacebuilder.BadlandsSurfaceBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BadlandsSurfaceBuilder.class)
public class MixinBadlandsSurfaceBuilder {
    @ModifyVariable(method = "initSeed", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private ChunkRandom redirectChunkRandom1(ChunkRandom rand) {
        ((IChunkRandom) rand).secureseed_setSeed(Globals.worldSeed, 0, 0, Globals.dimension.get(), Globals.SURFACE_BUILDER_SALT, 0);
        return rand;
    }

    @ModifyVariable(method = "initLayerBlocks", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private ChunkRandom redirectChunkRandom2(ChunkRandom rand) {
        ((IChunkRandom) rand).secureseed_setSeed(Globals.worldSeed, 0, 0, Globals.dimension.get(), Globals.SURFACE_BUILDER_SALT, 1);
        return rand;
    }
}
