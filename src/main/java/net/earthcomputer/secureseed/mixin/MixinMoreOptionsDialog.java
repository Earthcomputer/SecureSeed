package net.earthcomputer.secureseed.mixin;

import net.earthcomputer.secureseed.Globals;
import net.earthcomputer.secureseed.IGeneratorOptions;
import net.earthcomputer.secureseed.IMoreOptionsDialog;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.world.gen.GeneratorOptions;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalLong;

@Mixin(MoreOptionsDialog.class)
public class MixinMoreOptionsDialog implements IMoreOptionsDialog {
    @Shadow private TextFieldWidget seedTextField;
    @Unique private long[] secureSeed;

    @Inject(method = "method_28092",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/world/MoreOptionsDialog;seedTextField:Lnet/minecraft/client/gui/widget/TextFieldWidget;", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void setTextFieldLength(CallbackInfo ci) {
        seedTextField.setMaxLength(512);
    }

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "*",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/MoreOptionsDialog;method_30510(Ljava/util/OptionalLong;)Ljava/lang/String;")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;setText(Ljava/lang/String;)V", shift = At.Shift.AFTER))
    private void onSetSeedField(CallbackInfo ci) {
        if (secureSeed != null) {
            seedTextField.setText(Globals.seedToString(secureSeed));
        }
    }

    @Inject(method = "method_30511", at = @At("HEAD"))
    private void onSeedSaved(CallbackInfoReturnable<OptionalLong> ci) {
        String seedStr = seedTextField.getText();
        if (StringUtils.isEmpty(seedStr)) {
            secureSeed = null;
        } else {
            secureSeed = Globals.parseSeed(seedStr);
        }
    }

    @Inject(method = "getGeneratorOptions", at = @At("RETURN"))
    private void setSecureSeedOnGeneratorOptions(CallbackInfoReturnable<GeneratorOptions> ci) {
        ((IGeneratorOptions) ci.getReturnValue()).secureseed_setSeed(secureSeed);
    }

    @Override
    public void secureseed_setSeed(long[] seed) {
        this.secureSeed = seed;
    }
}
