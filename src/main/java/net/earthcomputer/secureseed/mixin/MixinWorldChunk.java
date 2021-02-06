package net.earthcomputer.secureseed.mixin;

import net.earthcomputer.secureseed.IWorldChunk;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.ChunkRandom;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WorldChunk.class)
public class MixinWorldChunk implements IWorldChunk {
    @Shadow @Final private ChunkPos pos;

    @Unique private boolean hasComputedSlimeChunk = false;
    @Unique private boolean isSlimeChunk;

    @Override
    public boolean secureseed_isSlimeChunk() {
        if (!hasComputedSlimeChunk) {
            hasComputedSlimeChunk = true;
            isSlimeChunk = ChunkRandom.getSlimeRandom(pos.x, pos.z, 0, 0).nextInt(10) == 0;
        }
        return isSlimeChunk;
    }
}
