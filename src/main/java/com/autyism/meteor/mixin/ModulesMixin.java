package com.autyism.meteor.mixin;

import com.autyism.meteor.modules.ReleaseBinds;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Meteor 模块热键：按下时不再切换，改为松开时按前置键规则切换（见 {@link ReleaseBinds}）。
 */
@Mixin(value = Modules.class, remap = false)
public abstract class ModulesMixin {
    @Inject(method = "onAction", at = @At("HEAD"), cancellable = true)
    private void am$releaseBinds(boolean isKey, int value, int modifiers, boolean isPress, CallbackInfo ci) {
        if (!ReleaseBinds.handlesModules()) return;
        ci.cancel();
        ReleaseBinds.handleModuleAction(((Modules) (Object) this).getAll(), isKey, value, isPress);
    }
}
