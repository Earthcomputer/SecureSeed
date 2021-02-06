package net.earthcomputer.secureseed.mixin;

import net.earthcomputer.secureseed.IGeneratorOptions;
import net.earthcomputer.secureseed.IMoreOptionsDialog;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;

@Mixin(CreateWorldScreen.class)
public class MixinCreateWorldScreen {
    @Shadow @Final public MoreOptionsDialog moreOptionsDialog;

    @Inject(method = "<init>(Lnet/minecraft/client/gui/screen/Screen;Lnet/minecraft/world/level/LevelInfo;Lnet/minecraft/world/gen/GeneratorOptions;Ljava/nio/file/Path;Lnet/minecraft/resource/DataPackSettings;Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;)V",
            at = @At("TAIL"))
    private void setSecureSeed(Screen screen, LevelInfo levelInfo, GeneratorOptions generatorOptions, Path path, DataPackSettings dataPackSettings, DynamicRegistryManager.Impl impl, CallbackInfo ci) {
        ((IMoreOptionsDialog) moreOptionsDialog).secureseed_setSeed(((IGeneratorOptions) generatorOptions).secureseed_getSeed());
    }
}
