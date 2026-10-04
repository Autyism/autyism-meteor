<p align="center"><img src="docs/icon.png" width="128" alt="icon"></p>
<h1 align="center">Autyism's Meteor Addon</h1>
<p align="center">Meteor Client keybinds that fire when you let go, and stay quiet during other mods' key combinations.</p>

**English** | [简体中文](README.zh-CN.md)

![Minecraft 1.21.11](https://img.shields.io/badge/Minecraft-1.21.11-62B47A) ![Fabric](https://img.shields.io/badge/Loader-Fabric-DBD0B4) ![Meteor Client addon](https://img.shields.io/badge/Meteor_Client-addon-913DE2) ![License: GPL-3.0-or-later](https://img.shields.io/badge/License-GPL--3.0--or--later-blue)

An addon for [Meteor Client](https://meteorclient.com) that changes when Meteor's module and macro keybinds fire. It adds one module, **Release Binds**: a bind fires when you *release* its key, and not at all if another key was already held when you pressed it. Key combinations meant for other mods stop toggling your Meteor modules by accident.

**The problem.** Meteor reacts the moment a bind's key goes down, and a bind on a single key also fires while you are holding other keys. Say a Meteor module is bound to `C`, and another mod uses `Ctrl + C`, or "hold `H`, then press `C`", for one of its own actions. Every time you use that combination, the Meteor module toggles as well. If `H` is also a Meteor bind, that module toggles as soon as you press `H`, before you have even reached `C`.

**With Release Binds on:**

- Tap `C` on its own: the module toggles when you let go.
- Hold `H`, press `C`, let go in any order: nothing toggles. `C` was pressed while `H` was held, and `H` was used as the first key of a combination.
- Hold `Ctrl`, press `C`: the plain `C` bind stays quiet. A Meteor bind set to `Ctrl + C` fires instead.

The other mod still receives every key as usual. Release Binds never blocks a key; it only decides when Meteor reacts.

## Features

### When binds fire

- **Fire on release.** Module binds and macros trigger when you let go of the key, not when you press it. This is what makes everything else possible: at the moment a key goes down, there is no way to know yet whether you are about to press a second key with it.
- **Hold-to-activate modules keep working.** Modules with Meteor's *Toggle on bind release* option still turn on when you press the key and off when you let go. They follow the prefix rule too: if another key was already held, the module does not turn on.
- **Menus and chat are left alone.** A key pressed while a menu, the chat or Meteor's own GUI is open never triggers a bind, even if you let go after the menu has closed. Closing your inventory with a key does not also toggle a module bound to that key.

### Key combinations

- **Any key can be a prefix.** If another key was held when you pressed a bind's key, the bind does not fire. This is not limited to Ctrl, Shift and Alt: letters, numbers, function keys and mouse buttons count as well.
- **The first key of a combination stays quiet too.** If you press another key while still holding a bind's key, that bind does not fire when you let go. With `H` then `C`, neither the `H` bind nor the `C` bind fires.
- **Exact modifiers.** A bind with modifiers, such as `Ctrl + X`, fires only when exactly those modifier keys are held and no other key. A plain `C` bind and a `Ctrl + C` bind can now sit side by side without both firing.
- **Walking and mining don't get in the way.** The keys bound to moving, jumping, sneaking, sprinting, attacking and using items don't count as prefix keys, so binds still fire while you walk or hold the mouse button to mine. This follows your current Minecraft controls and can be turned off. Ctrl, Shift, Alt and Super are the exception: they always count, even when one of them is your sneak or sprint key.

### What it covers

- **Every Meteor module**, including modules added by other Meteor addons.
- **Macros.** Meteor macros follow the same rules and run once when you let go of the key.
- **Mouse buttons.** Binds on mouse buttons (for example the side buttons) fire on release as well, and a held mouse button can act as a prefix.
- **Nothing else.** Meteor's Open GUI and Open Commands keys, assigning binds, key options inside a module's own settings, vanilla controls and other mods' keys all work exactly as before.

### Safe defaults

- **On from the first start.** Release Binds switches itself on the first time you start the game with the addon. After that, Meteor remembers whether you left it on or off.
- **Easy to undo.** Turning Release Binds off brings back Meteor's normal behaviour completely. You can also limit it to module binds or to macro binds only.
- **No stuck keys.** If the game misses a key release (for example when you Alt-Tab away while holding a key), that key does not stay "held" and block your binds.

## Screenshots

![Release Binds settings](docs/images/release-binds-settings.png)

*The Release Binds module and its settings in Meteor's GUI.*

## How to use

The addon adds no keys or commands of its own. Release Binds is a normal Meteor module, so you manage it like any other module.

### Keys

| Action | Default key | Where to change it |
|---|---|---|
| Open Meteor's ClickGUI (Release Binds is in the **Misc** window) | Right Shift | Minecraft Options → Controls → Key Binds → *Meteor Client* → **Open GUI** |
| Toggle a Meteor module | None (set your own) | Right-click the module in the ClickGUI → **Bind** |
| Run a Meteor macro | None (set your own) | ClickGUI → **Macros** tab → the macro's **Keybind** |

If you use Mod Menu, Meteor Client's config button opens the same ClickGUI.

### Commands

Meteor's own commands work with Release Binds (Meteor's default command prefix is `.`):

| Command | What it does |
|---|---|
| `.toggle release-binds` | Turns Release Binds on or off |
| `.settings release-binds` | Opens its settings |
| `.settings release-binds <setting> <value>` | Changes a setting, for example `.settings release-binds ignore-movement-keys false` |

### Getting started

1. Install the addon next to Meteor Client and Fabric API, and start the game.
2. Release Binds is already on. Your existing Meteor binds now fire when you release the key.
3. To look at its settings, press Right Shift, find **Release Binds** in the **Misc** window and right-click it. A left-click turns it on or off.

### Using another mod's key combination

1. Hold the first key of the combination, then press the second key.
2. Let go in any order. Neither key fires a Meteor bind, and the other mod still gets both keys.

### A module that is only on while you hold its key

1. Right-click the module in the ClickGUI.
2. Under **Bind**, tick **Toggle on bind release**.
3. Hold the key to keep the module on and let go to turn it off. If another key is already held when you press the key, the module stays off.

### What Release Binds changes

| Meteor feature | With Release Binds on |
|---|---|
| Module binds | Fire when released, following the prefix rules (setting **Module Binds**) |
| Modules with *Toggle on bind release* | On when pressed (only without a prefix key), off when released |
| Macro binds | Run once when released, following the prefix rules (setting **Macro Binds**) |
| Mouse button binds | Same rules as keyboard keys |
| Open GUI and Open Commands keys | Unchanged |
| Assigning a bind | Unchanged |
| Key options inside a module's own settings | Unchanged |
| Vanilla controls and other mods' keys | Unchanged; no key is ever blocked |

## Settings

Open them with Right Shift → **Misc** → right-click **Release Binds**, or with `.settings release-binds`.

| Setting | Command name | Default | What it does |
|---|---|---|---|
| Release Binds (the module itself) | `release-binds` | On (switched on at first start) | Turns the addon's behaviour on or off. When it is off, Meteor handles keys the normal way. |
| Module Binds | `module-binds` | On | Applies the release and prefix rules to module keybinds. |
| Macro Binds | `macro-binds` | On | Applies the release and prefix rules to macro keybinds. |
| Ignore Movement Keys | `ignore-movement-keys` | On | The keys bound to moving, jumping, sneaking, sprinting, attacking and using never count as prefix keys, so binds still fire while you walk or mine. Ctrl, Shift, Alt and Super (the Windows or Command key) always count, even when one of them is your sneak or sprint key. Turn this off to make every held key count. |

## Requirements

- Minecraft Java Edition 1.21.11
- Fabric Loader 0.17.0 or newer (Meteor Client 1.21.11 build 86 itself needs 0.18.2 or newer)
- [Fabric API](https://modrinth.com/mod/fabric-api)
- **Meteor Client for Minecraft 1.21.11 (required).** Meteor Client is not on Modrinth; download it from [meteorclient.com](https://meteorclient.com). This addon is built against Meteor Client build 86 (version `1.21.11-86`).
- Java 21 or newer

**Side:** client only. Servers do not need it, and it sends nothing to the server. It works in singleplayer and multiplayer.

## Compatibility

- **Other Meteor addons:** their modules follow the same rules as Meteor's own modules.
- **Other mods' keybinds:** never blocked or changed. The addon only decides when Meteor reacts to a key; vanilla and other mods still see every key press.
- **Rendering mods such as Sodium or Iris:** not affected. The addon only reads keyboard and mouse button input.
- **Meteor versions:** built against Meteor Client 1.21.11 build 86. The addon changes how Meteor handles module and macro binds, so a later Meteor build that reworks that part may need an updated version of this addon. If the game no longer starts after a Meteor update, remove the addon and please open an [issue](https://github.com/Autyism/autyism-meteor/issues).

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.11.
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) and [Meteor Client](https://meteorclient.com) for 1.21.11.
3. Download `autyism-meteor-addon-1.0.0.jar` from Modrinth or the GitHub releases.
4. Put all three jar files into the `mods` folder of your game directory (or of your launcher's instance).
5. Start the game. Release Binds is already on; you find it in Meteor's ClickGUI (Right Shift) under **Misc**.

## FAQ

**My module now toggles only when I let go of the key. Is that right?**

Yes. Only once you let go can the addon tell a single tap from the start of a key combination.

**A bind does nothing while I sneak or sprint.**

Ctrl, Shift, Alt and Super always count as prefix keys, even when they are your sneak or sprint keys (Minecraft's defaults are Left Shift for sneak and Left Ctrl for sprint). Let go of them before tapping the bind, or set Sneak or Sprint to "Toggle" in Minecraft's controls so you don't have to hold the key.

**I used a key combination and my Meteor module did not toggle.**

That is what the addon is for: if another key was held when you pressed the bind's key, or you pressed another key while holding it, the bind does not fire.

**Is this the same as Meteor's "Toggle on bind release" option?**

No. That option makes a single module active only while you hold its key. Release Binds changes when all binds fire and adds the prefix rule. Modules that use the option keep working as described above.

**Does it change the key that opens Meteor's GUI?**

No. The Open GUI and Open Commands keys work exactly as before.

**Does it work with modules from other Meteor addons?**

Yes. It applies to every Meteor module, whichever addon added it.

**How do I get Meteor's normal behaviour back?**

Turn Release Binds off (left-click it in the Misc window, or `.toggle release-binds`). To keep it for only one kind of bind, turn off **Module Binds** or **Macro Binds** instead.

**Release Binds was already on. Did I do that?**

It switches itself on the first time you start the game with the addon. After that, your choice is kept like for any other module.

**Does the server need it?**

No. It is client-only and sends nothing to the server.

## Known limitations

- Binds react when you release the key, so they fire a moment later than with Meteor's default behaviour. Modules with *Toggle on bind release* still turn on as soon as you press the key.
- Ctrl, Shift, Alt and Super always count as prefix keys. With Minecraft's default controls (sneak on Left Shift, sprint on Left Ctrl), a bind only fires while you hold the sneak or sprint key if it includes that key as a modifier (such as `Shift + C`).
- Any other held key blocks a bind, apart from the keys bound to moving, jumping, sneaking, sprinting, attacking and using. For example, holding the player list key or another mod's hold-to-use key (such as a zoom key) keeps binds from firing while it is held.
- Holding a macro key does not repeat the macro. It runs once each time you release the key.
- Built against Meteor Client 1.21.11 build 86. Later Meteor builds may need an update of this addon.

## Credits

- [Meteor Client](https://github.com/MeteorDevelopment/meteor-client) by MineGame159, squidoodly, seasnail and contributors: the client this addon extends.
- [Fabric](https://fabricmc.net) (Fabric Loader and Fabric API).

## License

GPL-3.0-or-later: this addon is free software under the GNU General Public License, version 3 or (at your option) any later version.
