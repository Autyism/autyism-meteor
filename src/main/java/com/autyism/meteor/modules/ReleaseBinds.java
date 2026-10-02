package com.autyism.meteor.modules;

import com.autyism.meteor.input.KeyTracker;
import com.autyism.meteor.mixin.KeybindAccessor;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import net.minecraft.client.Minecraft;

import java.util.Collection;

/**
 * 松开触发 + 前置键屏蔽：
 * <ul>
 *   <li>模块 / 宏的热键在<b>松开</b>时才触发；</li>
 *   <li>按下前已经按住了别的键（任意键，比如 H 再按 C）就不触发；</li>
 *   <li>按住期间又按了别的键（这个键当了前置键）也不触发；</li>
 *   <li>Meteor 里绑定了修饰键的热键（如 Ctrl+X）：按下时按住的修饰键必须正好是绑定的那几个，且没有别的前置键；</li>
 *   <li>“按住生效”的模块（toggle on bind release）按下即开、松开即关，同样遵守前置键规则。</li>
 * </ul>
 * 模块关闭时完全恢复 Meteor 原本的行为。
 */
public class ReleaseBinds extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> modules = sgGeneral.add(new BoolSetting.Builder()
            .name("module-binds")
            .description("Apply to module keybinds.")
            .defaultValue(true)
            .build());

    private final Setting<Boolean> macros = sgGeneral.add(new BoolSetting.Builder()
            .name("macro-binds")
            .description("Apply to macro keybinds.")
            .defaultValue(true)
            .build());

    private final Setting<Boolean> ignoreMovement = sgGeneral.add(new BoolSetting.Builder()
            .name("ignore-movement-keys")
            .description("Movement, jump, sneak, sprint, attack and use keys never count as a prefix key, so binds still work while walking or mining. Ctrl/Shift/Alt/Super always count.")
            .defaultValue(true)
            .build());

    public ReleaseBinds() {
        super(Categories.Misc, "release-binds", "Keybinds fire when released, and never when another key (any key) was held first: H then C triggers neither H nor C.");
    }

    private static ReleaseBinds instance() {
        Modules m = Modules.get();
        return m == null ? null : m.get(ReleaseBinds.class);
    }

    public static boolean handlesModules() {
        ReleaseBinds rb = instance();
        if (rb == null || !rb.isActive() || !rb.modules.get()) return false;
        KeyTracker.ignoreMovementKeys = rb.ignoreMovement.get();
        return true;
    }

    public static boolean handlesMacros() {
        ReleaseBinds rb = instance();
        if (rb == null || !rb.isActive() || !rb.macros.get()) return false;
        KeyTracker.ignoreMovementKeys = rb.ignoreMovement.get();
        return true;
    }

    /** 同一个键（不看修饰键） */
    public static boolean sameKey(Keybind bind, boolean isKey, int value) {
        return bind.isSet() && bind.isKey() == isKey && bind.getValue() == value;
    }

    /** 这次按下对这个绑定来说是不是“干净的”：没有别的前置键，修饰键与绑定完全一致，按下时没开界面 */
    public static boolean cleanFor(Keybind bind, KeyTracker.Hold hold) {
        int mods = bind.hasMods() ? ((KeybindAccessor) (Object) bind).am$getModifiers() : 0;
        return hold.otherPrefixes.isEmpty() && hold.modifierMask == mods && !hold.screenAtPress;
    }

    /** 替代 Modules.onAction */
    public static void handleModuleAction(Collection<Module> all, boolean isKey, int value, boolean isPress) {
        int code = KeyTracker.code(isKey, value);
        if (isPress) KeyTracker.press(code);
        else KeyTracker.release(code);
        KeyTracker.Hold hold = KeyTracker.get(code);
        if (hold == null) return;
        boolean noScreen = Minecraft.getInstance().screen == null;
        for (Module module : all.toArray(new Module[0])) {
            if (!sameKey(module.keybind, isKey, value)) continue;
            if (module.toggleOnBindRelease) {
                if (isPress) {
                    if (noScreen && !module.isActive() && cleanFor(module.keybind, hold)) toggle(module);
                } else if (module.isActive()) {
                    toggle(module);
                }
            } else if (!isPress && noScreen && !hold.usedAsPrefix && cleanFor(module.keybind, hold)) {
                toggle(module);
            }
        }
    }

    private static void toggle(Module module) {
        module.toggle();
        module.sendToggledMsg();
    }
}
