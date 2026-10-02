package com.autyism.meteor.mixin;

import com.autyism.meteor.input.KeyTracker;
import com.autyism.meteor.modules.ReleaseBinds;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.meteor.MouseClickEvent;
import meteordevelopment.meteorclient.systems.macros.Macro;
import meteordevelopment.meteorclient.systems.macros.Macros;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Meteor 宏：同样改成松开触发 + 前置键屏蔽。
 */
@Mixin(value = Macros.class, remap = false)
public abstract class MacrosMixin {
    @Shadow
    private List<Macro> macros;

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void am$onKey(KeyEvent event, CallbackInfo ci) {
        if (!ReleaseBinds.handlesMacros()) return;
        ci.cancel();
        am$handle(true, event.key(), event.action);
    }

    @Inject(method = "onMouse", at = @At("HEAD"), cancellable = true)
    private void am$onMouse(MouseClickEvent event, CallbackInfo ci) {
        if (!ReleaseBinds.handlesMacros()) return;
        ci.cancel();
        am$handle(false, event.button(), event.action);
    }

    private void am$handle(boolean isKey, int value, KeyAction action) {
        if (action == KeyAction.Repeat) return;
        int code = KeyTracker.code(isKey, value);
        if (action == KeyAction.Press) {
            KeyTracker.press(code);
            return;
        }
        KeyTracker.release(code);
        KeyTracker.Hold hold = KeyTracker.get(code);
        if (hold == null || hold.usedAsPrefix || Minecraft.getInstance().screen != null) return;
        for (Macro macro : macros.toArray(new Macro[0])) {
            var bind = macro.keybind.get();
            if (ReleaseBinds.sameKey(bind, isKey, value) && ReleaseBinds.cleanFor(bind, hold) && macro.onAction()) return;
        }
    }
}
