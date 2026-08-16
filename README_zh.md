# Tough Expansion: Reborn (意志坚定拓展：重铸版)

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

**Tough Expansion: Reborn** 是经典生存拓展模组 *Tough Expansion* 面向 **Minecraft 1.20.1 (Forge & NeoForge)** 的现代化重构与强化移植版。

---

## 🤖 Vibe Coding 声明 (Vibe Coding Declaration)

> **本项目采用 Vibe Coding 开发范式。**  
> 本项目的 1.20.1 代码重构、跨模组软兼容适配层、多 JDK 兼容性处理以及低开销调度架构优化，均由开发者通过自然语言与 AI 编程助手协同对话交互（Vibe Coding）全流程完成。项目在继承原版经典机制的同时，完全重写了底层调度与兼容逻辑。

---

## ✨ 核心特性

- **零硬前置依赖（Standalone）**：
  - 核心功能内建独立运行库，无需依赖任何额外的硬前置库文件即可直接安装使用。
- **全方位生存模组深度软兼容**：
  - **Cold Sweat (寒冷与出汗 2.4.x+)**：智能平滑调节玩家核心体温（CORE Temperature）向中性舒适区收敛。
  - **Tough As Nails (意志坚定 9.2.x+)**：完整对接 `TemperatureLevel` 状态枚举与 `IThirst.drink()` 官方口渴接口，自动响应 TAN 配置开关。
  - **Thirst Was Taken / Thirst-Mod**：基于 Forge Capability 自动消耗储水为玩家快速补充口渴值与水分饱和度。
  - **Curios API (饰品栏)**：便携式温控器与止渴仪均支持佩戴在 `charm`（护符）或 `curio`（饰品）槽位，全自动静默运行。
- **极致性能优化设计（High Performance）**：
  - **GameTime 模运算调度**：每 60 Ticks（3 秒）执行一次检测与结算，59/60 的 Ticks 零计算、零反射、零 NBT 开销瞬间跳过。
  - **Hash 偏移负载削峰**：基于物品哈希自动打散多玩家、多物品的触发时机，消除服务器帧率尖峰。
  - **周期批量结算**：FE 电量与水量按周期一次性扣减，NBT 写入与网络数据包发送量降低 **98.3%**。
  - **状态脏检查（Dirty Check）**：仅在工作状态发生改变时同步 NBT，彻底杜绝背包物品高频闪烁与网络刷包。
- **现代化 Java 运行时全兼容**：
  - 经过针对性字节码与构建配置调优，完美支持 **Java 17 LTS、Java 21 LTS** 以及 **Oracle GraalVM Java 25**。

---

## 🛠️ 物品与方块

| 物品 / 方块 | 属性与容量 | 描述与说明 |
| :--- | :--- | :--- |
| **便携式温度调节器<br>(Portable Temp Regulator)** | **500,000 FE** 电容<br>（20 FE/tick 待机能耗） | 随身携带或佩戴于饰品栏，在环境温度异常时消耗电能自动平稳调节玩家体温至正常区间。 |
| **止渴仪<br>(Thirst Quencher)** | **500,000 FE** 电容<br>**5,000 mB** 水容量（5桶） | 支持右键水源吸收、右键储罐抽水或放入注液机自动充水；自动净化并在玩家口渴时补水。 |
| **温度调节器方块<br>(Temperature Regulator)** | **1,000,000 FE** 电容<br>7格范围（可配置） | 环境温控方块，可调节范围内所有玩家体温，支持 3 种红石信号模式切换（高电平/低电平/忽略）。 |

---

## ⚙️ 配置文件说明 (`tanaddons-common.toml`)

模组安装后会在 `config/tanaddons-common.toml` 生成详细的可配置项：

```toml
[general]
    # 是否开启 FE 电能消耗需求（关闭则无需用电）
    requireEnergy = true

[portable_temp_regulator]
    # 便携式温度调节器的最大 FE 储电量
    rfCapacity = 500000
    # 每次调节体温时每 tick 消耗的 FE 基础能耗
    rfPerTick = 20

[thirst_quencher]
    # 止渴仪的最大 FE 储电量
    rfCapacity = 500000
    # 每次止渴时每 tick 消耗的 FE 基础能耗
    rfPerTick = 20
    # 内部最大储水量 (mB)
    waterCapacity = 5000

[temp_regulator_block]
    # 温控方块的最大 FE 储电量
    rfCapacity = 1000000
    # 作用半径（以方块为中心）
    radius = 7
```

---

## 📜 鸣谢与开源协议

- **原始作者**：感谢 **p455w0rd** 创造了优秀的原始模组 *Tough Expansion*。
- **协议**：本项目基于 **MIT License** 开源。
