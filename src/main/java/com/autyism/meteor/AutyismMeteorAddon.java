package com.autyism.meteor;

import com.autyism.meteor.modules.ReleaseBinds;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

/**
 * Autyism 的 Meteor 插件入口。以后所有给 Meteor 做的修改都放在这个插件里。
 */
public class AutyismMeteorAddon extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        LOG.info("Initializing Autyism's Meteor Addon");
        Modules.get().add(new ReleaseBinds());
    }

    @Override
    public String getPackage() {
        return "com.autyism.meteor";
    }
}
