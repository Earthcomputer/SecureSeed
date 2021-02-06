package net.earthcomputer.secureseed.mixin;

import net.earthcomputer.secureseed.Globals;
import net.earthcomputer.secureseed.IChunkRandom;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.world.gen.ChunkRandom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(EnderDragonFight.class)
public class MixinEnderDragonFight {
    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(method = "<init>", at = @At(value = "NEW", target = "(J)Ljava/util/Random;"))
    private Random redirectGatewayOrderRandom(long seed) {
        ChunkRandom rand = new ChunkRandom(0);
        //noinspection ConstantConditions
        ((IChunkRandom) rand).secureseed_setSeed(Globals.worldSeed, 0, 0, Globals.dimension.get(), Globals.END_GATEWAY_ORDER_SALT, 0);
        return rand;
    }
}
