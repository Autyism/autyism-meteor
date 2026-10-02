package com.autyism.meteor.mixin;

import com.autyism.meteor.input.KeyTracker;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 记录所有键盘按键（包括界面打开时、被 Meteor 取消的），给前置键判断用 */
@Mixin(value = KeyboardHandler.class, priority = 500)
public abstract class KeyboardHandlerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "keyPress", at = @At("HEAD"))
    private void am$track(long window, int action, KeyEvent event, CallbackInfo ci) {
        if (window != minecraft.getWindow().handle() || event.key() < 0) return;
        if (action == 1) KeyTracker.press(event.key());
        else if (action == 0) KeyTracker.release(event.key());
    }
}
