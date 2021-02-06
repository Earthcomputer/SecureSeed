package net.earthcomputer.secureseed.mixin;

import com.google.common.cache.LoadingCache;
import net.earthcomputer.secureseed.Globals;
import net.earthcomputer.secureseed.IChunkRandom;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(EndSpikeFeature.class)
public class MixinEndSpikeFeature {
    @Shadow @Final private static LoadingCache<Long, List<EndSpikeFeature.Spike>> CACHE;

    /**
     * @author Earthcomputer
     */
    @Overwrite
    public static List<EndSpikeFeature.Spike> getSpikes(StructureWorldAccess world) {
        if (world instanceof ServerWorld) {
            Globals.setupGlobals((ServerWorld) world);
        }
        ChunkRandom rand = new ChunkRandom(0);
        //noinspection ConstantConditions
        ((IChunkRandom) rand).secureseed_setSeed(Globals.worldSeed, 0, 0, Globals.dimension.get(), Globals.END_PILLAR_SALT, 0);
        return CACHE.getUnchecked(rand.nextLong());
    }
}
