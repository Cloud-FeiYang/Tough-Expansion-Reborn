# Tough Expansion: Reborn (意志坚定拓展：重铸版)

[![Minecraft 1.20.1](https://img.shields.io/badge/Minecraft-1.20.1-brightgreen.svg)](https://minecraft.net/)
[![Forge](https://img.shields.io/badge/Forge-47.3.0+-orange.svg)](https://files.minecraftforge.net/)
[![NeoForge](https://img.shields.io/badge/NeoForge-47.1.105+-blue.svg)](https://neoforged.net/)
[![License: GPL-3.0](https://img.shields.io/badge/License-GPL--3.0-blue.svg)](LICENSE)
[![Vibe Coding](https://img.shields.io/badge/Vibe%20Coding-AI%20Assisted-ff69b4.svg)](https://github.com/Cloud-FeiYang/Tough-Expansion-Reborn)

[English](README.md) | [简体中文](README_zh.md)

面向 **Minecraft 1.20.1 (Forge & NeoForge)** 环境生存模组的科技拓展。  
基于 p455w0rd 的原版 [Tough Expansion](https://github.com/p455w0rd/ToughExpansion) 移植与重构。

> 🤖 **Vibe Coding 声明**：本项目的 1.20.1 移植与多模组兼容层基于 Vibe Coding（人机协同辅助编程）全流程开发完成。

---

## 🛠️ 物品与内容

- **温度调节器方块（Temperature Regulator）**：消耗 FE 能量调节范围内玩家体温，支持 3 种红石控制模式。
- **便携式温度调节器（Portable Temp Regulator）**：可手持或放入 Curios 饰品栏，消耗 FE（500k FE）自动调节玩家体温。
- **止渴仪（Thirst Quencher）**：可手持或放入 Curios 饰品栏（500k FE, 5000 mB），支持右键补水与自动净化止渴。

### 🔌 模组联动
- **Tough As Nails (意志坚定 9.2.x+)**
- **Cold Sweat (寒冷与出汗 2.4.x+)**
- **Thirst Was Taken / Thirst-Mod**
- **Curios API (饰品栏)**

---

## ⚙️ 配置文件 (Configuration)

配置文件路径：  
📂 `.minecraft/config/tanaddons-common.toml`

```toml
[general]
    # 是否开启 FE 电能消耗需求（设为 false 则无需用电即可工作）
    requireEnergy = true

[portable_temp_regulator]
    # 便携式温度调节器的最大 FE 储电量 (范围: 1000 ~ 100000000)
    rfCapacity = 500000
    # 处于调节状态时每 tick 消耗的 FE 基础能耗 (范围: 0 ~ 100000)
    rfPerTick = 20

[thirst_quencher]
    # 止渴仪的最大 FE 储电量 (范围: 1000 ~ 100000000)
    rfCapacity = 500000
    # 处于止渴状态时每 tick 消耗的 FE 基础能耗 (范围: 0 ~ 100000)
    rfPerTick = 20
    # 内部最大储水量，单位 mB (默认 5000 = 5 桶水，范围: 1000 ~ 64000)
    waterCapacity = 5000

[temp_regulator_block]
    # 温度调节器方块的最大 FE 储电量 (范围: 1000 ~ 100000000)
    rfCapacity = 1000000
    # 每调节一个玩家每 tick 消耗的 FE 基础能耗 (范围: 0 ~ 100000)
    rfPerTick = 40
    # 调节作用半径，以方块为中心 (范围: 1 ~ 64)
    radius = 7
```

---

## 📜 开源协议

本项目基于 **[GNU General Public License v3.0 (GPL-3.0)](LICENSE)** 协议开源。  
部分代码基于 p455w0rd 的原始代码（基于 MIT License）。
