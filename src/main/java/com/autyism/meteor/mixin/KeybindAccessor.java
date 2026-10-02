package com.autyism.meteor.mixin;

import meteordevelopment.meteorclient.utils.misc.Keybind;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Keybind.class, remap = false)
public interface KeybindAccessor {
    @Accessor("modifiers")
    int am$getModifiers();
}
