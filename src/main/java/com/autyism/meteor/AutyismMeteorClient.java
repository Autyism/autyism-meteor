package com.autyism.meteor;

import com.autyism.meteor.modules.ReleaseBinds;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 第一次安装时自动打开 Release Binds（之后尊重用户在 Meteor 里的开关，Meteor 自己会保存模块状态）。
 */
public class AutyismMeteorClient implements ClientModInitializer {
    private static final Path MARKER = FabricLoader.getInstance().getConfigDir().resolve("autyism-meteor-addon.txt");
    private static boolean checked;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (checked) return;
            checked = true;
            try {
                if (Files.exists(MARKER)) return;
                ReleaseBinds module = Modules.get().get(ReleaseBinds.class);
                if (module != null && !module.isActive()) module.enable();
                Files.writeString(MARKER, "release-binds enabled by default on first run\n");
            } catch (Exception e) {
                AutyismMeteorAddon.LOG.warn("Could not enable Release Binds on first run", e);
            }
        });
    }
}
