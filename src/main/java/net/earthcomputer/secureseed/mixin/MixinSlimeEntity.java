package net.earthcomputer.secureseed.mixin;

import net.earthcomputer.secureseed.IWorldChunk;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(SlimeEntity.class)
public class MixinSlimeEntity extends MobEntity {
    protected MixinSlimeEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "canSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/ChunkRandom;getSlimeRandom(IIJJ)Ljava/util/Random;"))
    private static Random cacheSlimeChunkComputation(int chunkX, int chunkZ, long worldSeed, long scrambler, EntityType<SlimeEntity> type, WorldAccess world) {
        boolean isSlimeChunk = ((IWorldChunk) world.getChunk(chunkX, chunkZ)).secureseed_isSlimeChunk();
        //noinspection MixinInnerClass
        return new Random(0) {
            @Override
            public int nextInt(int bound) {
                return isSlimeChunk ? 0 : 1;
            }
        };
    }
}
