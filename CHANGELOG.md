# Changelog

## 1.0.0 — 2026-10-04

First public release.

- New Meteor module **Release Binds** (Misc category).
- Module and macro keybinds fire when the key is released instead of when it is pressed.
- Any key can act as a prefix: a bind does not fire if another key was already held when its key was pressed, and a key used as the first key of a combination does not fire its own bind when released.
- Binds with modifiers (for example `Ctrl + X`) fire only when exactly those modifier keys are held; a plain `C` bind and a `Ctrl + C` bind no longer fire together.
- Keys bound to moving, jumping, sneaking, sprinting, attacking and using do not count as prefix keys (setting **Ignore Movement Keys**, on by default); Ctrl, Shift, Alt and Super always count.
- Modules with Meteor's *Toggle on bind release* option turn on when pressed (only without a prefix key) and off when released.
- Works for mouse button binds and for every Meteor module, including modules from other addons.
- Separate on/off settings for module binds (**Module Binds**) and macro binds (**Macro Binds**).
- Keys pressed while a menu or the chat is open never trigger binds.
- Release Binds is switched on automatically the first time the game starts with the addon; turning it off restores Meteor's normal behaviour.

### 中文

首个公开版本。

- 新增 Meteor 模块 **Release Binds**（Misc 分类）。
- 模块和宏的快捷键改为松开按键时触发，而不是按下时。
- 任何键都能当前置键：按下快捷键时已经按住了别的键，这个快捷键就不触发；当过组合键第一个键的按键，松开时也不会触发自己的快捷键。
- 带修饰键的快捷键（如 `Ctrl + X`）只有在按住的修饰键完全一致时才触发；只绑 `C` 的快捷键和 `Ctrl + C` 的快捷键不再一起触发。
- 绑在移动、跳跃、潜行、疾跑、攻击、使用上的键不算前置键（设置 **Ignore Movement Keys**，默认开启）；Ctrl、Shift、Alt 和 Super 永远算前置键。
- 开了 Meteor「Toggle on bind release」选项的模块：按下开启（仅在没有前置键时），松开关闭。
- 支持鼠标按键快捷键，适用于所有 Meteor 模块，包括其他插件添加的模块。
- 模块快捷键（**Module Binds**）和宏快捷键（**Macro Binds**）可以分别开关。
- 在菜单或聊天栏打开时按下的键永远不会触发快捷键。
- 第一次带着插件启动游戏时 Release Binds 自动开启；关闭它即可完全恢复 Meteor 原本的行为。
