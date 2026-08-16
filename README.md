# Tough Expansion: Reborn

[![Minecraft 1.20.1](https://img.shields.io/badge/Minecraft-1.20.1-brightgreen.svg)](https://minecraft.net/)
[![Forge](https://img.shields.io/badge/Forge-47.3.0+-orange.svg)](https://files.minecraftforge.net/)
[![NeoForge](https://img.shields.io/badge/NeoForge-47.1.105+-blue.svg)](https://neoforged.net/)
[![License: GPL-3.0](https://img.shields.io/badge/License-GPL--3.0-blue.svg)](LICENSE)
[![Vibe Coding](https://img.shields.io/badge/Vibe%20Coding-AI%20Assisted-ff69b4.svg)](https://github.com/Cloud-FeiYang/Tough-Expansion-Reborn)

[English](README.md) | [简体中文](README_zh.md)

A modern tech addon for survival mods in **Minecraft 1.20.1 (Forge & NeoForge)**.  
Ported and enhanced from the original [Tough Expansion](https://github.com/p455w0rd/ToughExpansion) by p455w0rd.

> 🤖 **Vibe Coding Notice**: This port and its cross-mod compatibility layer were developed using Vibe Coding (AI-assisted pair programming).

---

## 🛠️ Features & Content

- **Temperature Regulator (Block)**: FE-powered environmental temperature regulator with configurable redstone modes.
- **Portable Temperature Regulator (Item)**: Handheld or Curios accessory that stabilizes player body temperature using FE (500k FE).
- **Thirst Quencher (Item)**: Handheld or Curios accessory (500k FE, 5,000 mB) that purifies stored water and automatically quenches thirst.

### 🔌 Compatibility
- **Tough As Nails** (9.2.x+)
- **Cold Sweat** (2.4.x+)
- **Thirst Was Taken** / **Thirst-Mod**
- **Curios API** (Equippable in `curio` / `charm` slots)

---

## ⚙️ Configuration

Config file location:  
📂 `.minecraft/config/tanaddons-common.toml`

```toml
[general]
    # Whether temperature regulation and thirst quenching require Forge Energy
    requireEnergy = true

[portable_temp_regulator]
    # FE storage capacity of the Portable Temperature Regulator (Range: 1000 ~ 100000000)
    rfCapacity = 500000
    # FE consumed per tick while regulating player temperature (Range: 0 ~ 100000)
    rfPerTick = 20

[thirst_quencher]
    # FE storage capacity of the Thirst Quencher (Range: 1000 ~ 100000000)
    rfCapacity = 500000
    # FE consumed per tick while quenching player thirst (Range: 0 ~ 100000)
    rfPerTick = 20
    # Internal water storage capacity in mB (default 5000 = 5 buckets, Range: 1000 ~ 64000)
    waterCapacity = 5000

[temp_regulator_block]
    # FE storage capacity of the Temperature Regulator block (Range: 1000 ~ 100000000)
    rfCapacity = 1000000
    # FE consumed per tick per player being regulated by the block (Range: 0 ~ 100000)
    rfPerTick = 40
    # Block radius within which players are regulated (Range: 1 ~ 64)
    radius = 7
```

---

## 📜 License

Distributed under the [GNU General Public License v3.0 (GPL-3.0)](LICENSE).  
Portions based on Tough Expansion by p455w0rd (under MIT License).
