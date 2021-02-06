package net.earthcomputer.secureseed.mixin;

import net.earthcomputer.secureseed.Globals;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerChunkManager.class)
public class MixinServerChunkManager {
    @Shadow @Final private ServerWorld world;

    @Inject(method = "getChunkGenerator", at = @At("HEAD"))
    private void onGetChunkGenerator(CallbackInfoReturnable<ChunkGenerator> ci) {
        Globals.setupGlobals(world);
    }
}
