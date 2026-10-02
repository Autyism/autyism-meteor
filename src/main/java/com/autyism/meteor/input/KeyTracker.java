package com.autyism.meteor.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import org.lwjgl.glfw.GLFW;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 记录当前按住的所有键（键盘 + 鼠标），以及每个键按下那一刻已经按住了哪些“前置键”。
 * <p>
 * 任何键都可以是前置键（不只是 Ctrl/Alt/Shift）：先按住 H 再按 C，C 的这次按下就带有前置键 H，
 * 同时 H 被标记为“当过前置键”，松开 H 时也不会触发 H 的绑定。
 * <p>
 * 例外（可在模块设置里关闭）：移动、跳跃、攻击、使用这些原版持续按住的键不算前置键，
 * 否则边走路边按热键就永远不会触发。Ctrl / Shift / Alt / Super 永远算前置键（它们就是组合键）。
 * <p>
 * 键编码：键盘键 = GLFW 键码；鼠标键 = {@link #MOUSE_BASE} + 按钮号。
 */
public final class KeyTracker {
    public static final int MOUSE_BASE = 1000;
    private static final Minecraft mc = Minecraft.getInstance();

    /** 一次按下的记录 */
    public static final class Hold {
        /** 按下时已按住的非修饰前置键 */
        public final List<Integer> otherPrefixes = new ArrayList<>();
        /** 按下时已按住的修饰键（Meteor 的 modifiers 位） */
        public int modifierMask;
        /** 按下时有界面打开（比如关背包的 E），松开时不触发 */
        public boolean screenAtPress;
        /** 按住期间又按了别的键：这个键当了前置键，松开时不触发 */
        public boolean usedAsPrefix;

        public boolean hasPrefix() {
            return !otherPrefixes.isEmpty() || modifierMask != 0;
        }

        @Override
        public String toString() {
            return "Hold{prefixes=" + otherPrefixes + ", mods=" + modifierMask + ", screen=" + screenAtPress + ", usedAsPrefix=" + usedAsPrefix + "}";
        }
    }

    private static final Map<Integer, Hold> HELD = new LinkedHashMap<>();
    private static int lastReleasedCode = Integer.MIN_VALUE;
    @Nullable
    private static Hold lastReleased;
    /** 由 ReleaseBinds 模块设置 */
    public static boolean ignoreMovementKeys = true;

    private KeyTracker() {
    }

    public static int code(boolean isKey, int value) {
        return isKey ? value : MOUSE_BASE + value;
    }

    public static boolean isModifierKey(int code) {
        return code >= GLFW.GLFW_KEY_LEFT_SHIFT && code <= GLFW.GLFW_KEY_RIGHT_SUPER;
    }

    public static int modifierBit(int code) {
        return switch (code) {
            case GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT -> GLFW.GLFW_MOD_SHIFT;
            case GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL -> GLFW.GLFW_MOD_CONTROL;
            case GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_RIGHT_ALT -> GLFW.GLFW_MOD_ALT;
            case GLFW.GLFW_KEY_LEFT_SUPER, GLFW.GLFW_KEY_RIGHT_SUPER -> GLFW.GLFW_MOD_SUPER;
            default -> 0;
        };
    }

    /** 不算前置键的原版“持续按住”键（修饰键除外） */
    public static boolean isIgnored(int code) {
        if (!ignoreMovementKeys || isModifierKey(code) || mc.options == null) return false;
        KeyMapping[] keys = {mc.options.keyUp, mc.options.keyDown, mc.options.keyLeft, mc.options.keyRight,
                mc.options.keyJump, mc.options.keyShift, mc.options.keySprint, mc.options.keyAttack, mc.options.keyUse};
        for (KeyMapping k : keys) {
            InputConstants.Key bound = KeyBindingHelper.getBoundKeyOf(k);
            int c = bound.getType() == InputConstants.Type.MOUSE ? MOUSE_BASE + bound.getValue() : bound.getValue();
            if (bound.getType() != InputConstants.Type.SCANCODE && c == code) return true;
        }
        return false;
    }

    public static synchronized void press(int code) {
        if (code < 0) return;
        pruneReleased();
        if (HELD.containsKey(code)) return; // 重复事件
        Hold hold = new Hold();
        hold.screenAtPress = mc.screen != null;
        boolean ignored = isIgnored(code);
        for (Map.Entry<Integer, Hold> e : HELD.entrySet()) {
            int other = e.getKey();
            if (isIgnored(other)) continue;
            if (isModifierKey(other)) hold.modifierMask |= modifierBit(other);
            else hold.otherPrefixes.add(other);
            if (!ignored) e.getValue().usedAsPrefix = true;
        }
        HELD.put(code, hold);
    }

    public static synchronized void release(int code) {
        Hold hold = HELD.remove(code);
        if (hold != null) {
            lastReleasedCode = code;
            lastReleased = hold;
        }
    }

    /** 当前按住的键的记录；刚松开的键返回松开前的记录 */
    @Nullable
    public static synchronized Hold get(int code) {
        Hold hold = HELD.get(code);
        if (hold != null) return hold;
        return lastReleasedCode == code ? lastReleased : null;
    }

    public static synchronized boolean isHeld(int code) {
        return HELD.containsKey(code);
    }

    public static synchronized void clear() {
        HELD.clear();
        lastReleased = null;
        lastReleasedCode = Integer.MIN_VALUE;
    }

    /** 窗口失焦等情况可能丢掉松开事件：按新键前把实际已经松开的键清掉，避免“卡住的前置键”挡住所有热键 */
    private static void pruneReleased() {
        if (mc.getWindow() == null) return;
        long handle = mc.getWindow().handle();
        Iterator<Integer> it = HELD.keySet().iterator();
        while (it.hasNext()) {
            int code = it.next();
            boolean down;
            if (code >= MOUSE_BASE) down = GLFW.glfwGetMouseButton(handle, code - MOUSE_BASE) == GLFW.GLFW_PRESS;
            else down = InputConstants.isKeyDown(mc.getWindow(), code);
            if (!down) it.remove();
        }
    }
}
