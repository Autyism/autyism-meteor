package com.autyism.meteor.mixin;

import com.autyism.meteor.input.KeyTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 记录所有鼠标按键，给前置键判断用（鼠标侧键也可以是前置键 / 热键） */
@Mixin(value = MouseHandler.class, priority = 500)
public abstract class MouseHandlerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "onButton", at = @At("HEAD"))
    private void am$track(long window, MouseButtonInfo info, int action, CallbackInfo ci) {
        if (window != minecraft.getWindow().handle()) return;
        int code = KeyTracker.code(false, info.button());
        if (action == 1) KeyTracker.press(code);
        else if (action == 0) KeyTracker.release(code);
    }
}
