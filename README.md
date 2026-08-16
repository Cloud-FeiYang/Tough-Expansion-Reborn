# Tough Expansion: Reborn

<div align="center">

[![Minecraft 1.20.1](https://img.shields.io/badge/Minecraft-1.20.1-brightgreen.svg)](https://minecraft.net/)
[![Forge](https://img.shields.io/badge/Forge-47.3.0+-orange.svg)](https://files.minecraftforge.net/)
[![NeoForge](https://img.shields.io/badge/NeoForge-47.1.105+-blue.svg)](https://neoforged.net/)
[![Java 17 | 21 | 25](https://img.shields.io/badge/Java-17%20%7C%2021%20%7C%2025-blueviolet.svg)](https://www.oracle.com/java/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Vibe Coding](https://img.shields.io/badge/Developed%20with-Vibe%20Coding-ff69b4.svg)](https://github.com/Cloud-FeiYang/Tough-Expansion-Reborn)

[English](README.md) | [简体中文](README_zh.md)

</div>

---

**Tough Expansion: Reborn** is a modern port and enhancement of the classic *Tough Expansion* mod for **Minecraft 1.20.1 Forge & NeoForge**.

---

## 🤖 Vibe Coding Declaration

> **This project was created through Vibe Coding.**  
> The 1.20.1 porting, cross-mod soft compatibility layer, multi-JDK runtime adaptations, and low-overhead scheduling architecture optimizations were collaboratively designed and implemented through AI-assisted pair programming (Vibe Coding).

---

## ✨ Features

- **Standalone Operation (Zero Hard Dependencies)**:
  - Can run completely standalone in Forge/NeoForge with zero required third-party libraries.
- **Comprehensive Soft Dependency Compatibility**:
  - **Cold Sweat (2.4.x+)**: Smoothly regulates player core body temperature towards comfortable neutral without wiping status effects.
  - **Tough As Nails (9.2.x+)**: Full integration with `TemperatureLevel` enums and standard `IThirst.drink()` API.
  - **Thirst Was Taken / Thirst-Mod**: Automatically purifies stored water and restores player thirst and hydration.
  - **Curios API**: Portable Temperature Regulator and Thirst Quencher can be equipped in Curios accessory slots (`curio`, `charm`).
- **High-Performance Low-Overhead Architecture**:
  - **GameTime Modulo Scheduling**: Evaluates once every 60 ticks (3 seconds), skipping 59/60 ticks with zero NBT or reflection overhead.
  - **Hash Offset Load Smoothing**: Distributes tick execution evenly across multiple items and players.
  - **Batch Resource Deduction**: FE energy and fluid are deducted per cycle, reducing NBT writes and slot packets by **98.3%**.
  - **Dirty Checking**: Only modifies ItemStack NBT tags on real state transitions, completely eliminating inventory flicker and packet spam.
- **Modern Java Compatibility**:
  - Verified and optimized for **Java 17 LTS, Java 21 LTS, and Oracle GraalVM Java 25**.

---

## 🛠️ Items & Blocks

- **Temperature Regulator (Block)**: FE-powered environmental temperature regulator (1,000,000 FE capacity) with 3 switchable Redstone modes.
- **Portable Temperature Regulator (Item)**: Handheld or Curios accessory with **500,000 FE** capacity that stabilizes player body temperature using FE.
- **Thirst Quencher (Item)**: Handheld or Curios accessory with **500,000 FE** and **5,000 mB** internal water reservoir with right-click water collection and automatic thirst quenching.

---

## 📜 License

Distributed under the MIT License. Based on Tough Expansion by p455w0rd.
