package com.autyism.meteor.gametest;

import com.autyism.meteor.modules.ReleaseBinds;
import meteordevelopment.meteorclient.systems.macros.Macro;
import meteordevelopment.meteorclient.systems.macros.Macros;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.gui.screens.ChatScreen;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Release Binds：松开触发、任意前置键屏蔽、修饰键绑定、走路时仍可用、按住型模块、宏、鼠标键、界面、关闭模块恢复原行为。
 * 全部用真实的模拟按键（经过原版 KeyboardHandler / MouseHandler → Meteor 的事件 → 本插件）。
 */
@SuppressWarnings("UnstableApiUsage")
public final class ReleaseBindsGameTest implements FabricClientGameTest {
    private static final int C = GLFW.GLFW_KEY_C, H = GLFW.GLFW_KEY_H, X = GLFW.GLFW_KEY_X, V = GLFW.GLFW_KEY_V, M = GLFW.GLFW_KEY_M;
    private static final int MOUSE4 = GLFW.GLFW_MOUSE_BUTTON_4;
    private final List<String> failures = new ArrayList<>();

    private static void log(String s) {
        System.out.println("[AM-GT] " + s);
    }

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext sp = context.worldBuilder().create()) {
            context.waitTicks(20);
            boolean defaultOn = context.computeOnClient(c -> Modules.get().get(ReleaseBinds.class).isActive());
            check("release-binds enabled by default on first run", defaultOn);

            context.runOnClient(c -> {
                bind("auto-respawn", Keybind.fromKey(C), false);
                bind("anti-hunger", Keybind.fromKey(H), false);
                bind("safe-walk", Keybind.fromKeys(X, GLFW.GLFW_MOD_CONTROL), false);
                bind("no-fall", Keybind.fromKey(V), true);
                bind("no-slow", Keybind.fromButton(MOUSE4), false);
                Macro macro = new Macro();
                macro.name.set("am-test");
                macro.messages.set(List.of(".toggle fast-climb"));
                macro.keybind.set(Keybind.fromKey(M));
                Macros.get().add(macro);
            });

            // 1) 单键：按下不触发，松开触发
            context.getInput().holdKey(C);
            context.waitTicks(3);
            check("C press does not toggle", !active(context, "auto-respawn"));
            context.getInput().releaseKey(C);
            context.waitTicks(3);
            check("C release toggles", active(context, "auto-respawn"));
            reset(context);

            // 2) H 再 C：两个都不触发（任意键都是前置键）
            context.getInput().holdKey(H);
            context.waitTicks(2);
            context.getInput().holdKey(C);
            context.waitTicks(2);
            context.getInput().releaseKey(C);
            context.waitTicks(2);
            context.getInput().releaseKey(H);
            context.waitTicks(3);
            check("H+C toggles neither C", !active(context, "auto-respawn"));
            check("H+C toggles neither H", !active(context, "anti-hunger"));
            // 先松 H 再松 C 也一样
            context.getInput().holdKey(H);
            context.waitTicks(2);
            context.getInput().holdKey(C);
            context.waitTicks(2);
            context.getInput().releaseKey(H);
            context.waitTicks(2);
            context.getInput().releaseKey(C);
            context.waitTicks(3);
            check("H+C (release H first) toggles neither C", !active(context, "auto-respawn"));
            check("H+C (release H first) toggles neither H", !active(context, "anti-hunger"));
            // H 单独按仍然有效
            tap(context, H);
            check("H alone toggles", active(context, "anti-hunger"));
            reset(context);

            // 3) Ctrl+C 不触发 C；Ctrl+X 触发 Ctrl+X 绑定；单独 X 不触发 Ctrl+X 绑定
            context.getInput().holdControl();
            context.waitTicks(2);
            tap(context, C);
            context.getInput().releaseControl();
            context.waitTicks(3);
            check("Ctrl+C does not toggle C bind", !active(context, "auto-respawn"));
            context.getInput().holdControl();
            context.waitTicks(2);
            tap(context, X);
            context.getInput().releaseControl();
            context.waitTicks(3);
            check("Ctrl+X toggles Ctrl+X bind", active(context, "safe-walk"));
            reset(context);
            tap(context, X);
            check("X alone does not toggle Ctrl+X bind", !active(context, "safe-walk"));
            // Ctrl+H+X：多了 H，不触发
            context.getInput().holdControl();
            context.getInput().holdKey(H);
            context.waitTicks(2);
            tap(context, X);
            context.getInput().releaseKey(H);
            context.getInput().releaseControl();
            context.waitTicks(3);
            check("Ctrl+H+X does not toggle Ctrl+X bind", !active(context, "safe-walk"));
            reset(context);

            // 4) 走路（按住 W）时按 C 仍然触发；按住 C 时再按 W 也仍然触发
            context.getInput().holdKey(GLFW.GLFW_KEY_W);
            context.waitTicks(3);
            tap(context, C);
            context.getInput().releaseKey(GLFW.GLFW_KEY_W);
            context.waitTicks(3);
            check("W held + C toggles", active(context, "auto-respawn"));
            reset(context);
            context.getInput().holdKey(C);
            context.waitTicks(2);
            context.getInput().holdKey(GLFW.GLFW_KEY_W);
            context.waitTicks(2);
            context.getInput().releaseKey(GLFW.GLFW_KEY_W);
            context.getInput().releaseKey(C);
            context.waitTicks(3);
            check("C held + W toggles", active(context, "auto-respawn"));
            reset(context);
            // 挖掘中（按住左键）按 C 也能触发
            context.getInput().holdMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
            context.waitTicks(2);
            tap(context, C);
            context.getInput().releaseMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
            context.waitTicks(3);
            check("attack held + C toggles", active(context, "auto-respawn"));
            reset(context);

            // 5) 按住型模块：按下开、松开关；有前置键时不开
            context.getInput().holdKey(V);
            context.waitTicks(3);
            check("hold module on while held", active(context, "no-fall"));
            context.getInput().releaseKey(V);
            context.waitTicks(3);
            check("hold module off after release", !active(context, "no-fall"));
            context.getInput().holdKey(H);
            context.waitTicks(2);
            context.getInput().holdKey(V);
            context.waitTicks(3);
            check("hold module not on with prefix H", !active(context, "no-fall"));
            context.getInput().releaseKey(V);
            context.getInput().releaseKey(H);
            context.waitTicks(3);
            reset(context);

            // 6) 宏：松开触发；有前置键不触发
            context.getInput().holdKey(M);
            context.waitTicks(3);
            check("macro press does not run", !active(context, "fast-climb"));
            context.getInput().releaseKey(M);
            context.waitTicks(5);
            check("macro release runs", active(context, "fast-climb"));
            reset(context);
            context.getInput().holdKey(H);
            context.waitTicks(2);
            tap(context, M);
            context.getInput().releaseKey(H);
            context.waitTicks(5);
            check("H+M macro does not run", !active(context, "fast-climb"));
            reset(context);

            // 7) 鼠标侧键
            context.getInput().holdMouse(MOUSE4);
            context.waitTicks(3);
            check("mouse4 press does not toggle", !active(context, "no-slow"));
            context.getInput().releaseMouse(MOUSE4);
            context.waitTicks(3);
            check("mouse4 release toggles", active(context, "no-slow"));
            reset(context);
            context.getInput().holdKey(H);
            context.waitTicks(2);
            context.getInput().holdMouse(MOUSE4);
            context.waitTicks(2);
            context.getInput().releaseMouse(MOUSE4);
            context.getInput().releaseKey(H);
            context.waitTicks(3);
            check("H+mouse4 does not toggle", !active(context, "no-slow"));
            reset(context);

            // 8) 界面打开时按下、关掉后才松开：不触发
            context.runOnClient(c -> c.setScreen(new ChatScreen("", false)));
            context.waitTicks(3);
            context.getInput().holdKey(C);
            context.waitTicks(2);
            context.runOnClient(c -> c.setScreen(null));
            context.waitTicks(2);
            context.getInput().releaseKey(C);
            context.waitTicks(3);
            check("key pressed inside a screen does not toggle after closing", !active(context, "auto-respawn"));
            reset(context);

            // 9) 关闭 release-binds：恢复 Meteor 原本“按下就触发”
            context.runOnClient(c -> Modules.get().get(ReleaseBinds.class).disable());
            context.getInput().holdKey(C);
            context.waitTicks(3);
            check("module off: press toggles (Meteor default)", active(context, "auto-respawn"));
            context.getInput().releaseKey(C);
            context.waitTicks(3);
            check("module off: release keeps it on", active(context, "auto-respawn"));
            context.runOnClient(c -> Modules.get().get(ReleaseBinds.class).enable());
            reset(context);
        }
        if (!failures.isEmpty()) throw new AssertionError("[release-binds] failed: " + failures);
        log("[release-binds] OK: all checks passed");
    }

    private void check(String name, boolean ok) {
        log((ok ? "PASS " : "FAIL ") + name);
        if (!ok) failures.add(name);
    }

    private static void tap(ClientGameTestContext context, int key) {
        context.getInput().holdKey(key);
        context.waitTicks(2);
        context.getInput().releaseKey(key);
        context.waitTicks(3);
    }

    private static void bind(String name, Keybind keybind, boolean hold) {
        Module m = Modules.get().get(name);
        if (m == null) throw new AssertionError("no module " + name);
        m.keybind.set(keybind);
        m.toggleOnBindRelease = hold;
        m.chatFeedback = false;
    }

    private static boolean active(ClientGameTestContext context, String name) {
        return context.computeOnClient(c -> Modules.get().get(name).isActive());
    }

    private static void reset(ClientGameTestContext context) {
        context.runOnClient(c -> {
            for (String n : new String[]{"auto-respawn", "anti-hunger", "safe-walk", "no-fall", "no-slow", "fast-climb"}) {
                Module m = Modules.get().get(n);
                if (m.isActive()) m.toggle();
            }
        });
        context.waitTicks(2);
    }
}
