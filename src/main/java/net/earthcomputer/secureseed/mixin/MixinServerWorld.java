package net.earthcomputer.secureseed.mixin;

import net.earthcomputer.secureseed.Globals;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.level.ServerWorldProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerWorld.class)
public class MixinServerWorld {
    @ModifyVariable(method = "<init>", at = @At(value = "LOAD", ordinal = 1), ordinal = 0)
    private ServerWorldProperties onInitServerWorld(ServerWorldProperties properties) {
        Globals.setupGlobals((ServerWorld) (Object) this);
        return properties;
    }
}
