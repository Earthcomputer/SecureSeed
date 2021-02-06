package net.earthcomputer.secureseed.mixin;

import net.earthcomputer.secureseed.Globals;
import net.earthcomputer.secureseed.IChunkRandom;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.surfacebuilder.FrozenOceanSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.NetherForestSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.NetherSurfaceBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({FrozenOceanSurfaceBuilder.class, NetherForestSurfaceBuilder.class, NetherSurfaceBuilder.class})
public class MixinSurfaceBuilders {
    @Redirect(method = "initSeed", at = @At(value = "NEW", target = "(J)Lnet/minecraft/world/gen/ChunkRandom;"))
    private ChunkRandom redirectChunkRandom(long seed) {
        ChunkRandom rand = new ChunkRandom(0);
        //noinspection ConstantConditions
        ((IChunkRandom) rand).secureseed_setSeed(Globals.worldSeed, 0, 0, Globals.dimension.get(), Globals.SURFACE_BUILDER_SALT, 0);
        return rand;
    }
}
