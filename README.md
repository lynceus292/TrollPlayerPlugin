
# LynceusTroll

[![Platform](https://img.shields.io/badge/Platform-Paper%20%281.21%2B%29-9cf.svg)](https://papermc.io)
[![Java](https://img.shields.io/badge/Java-21%2B-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-Open%20Source-brightgreen.svg)](#)

The ultimate troll toolkit for **Paper 1.21+** Minecraft servers. Crash, freeze, and bamboozle your players—all managed seamlessly through a single command. 

*Designed strictly for server administrators looking to play lighthearted jokes on their player base.*

---

## ✨ Features & Commands

All commands follow the primary usage syntax:
```bash
/troll <player> <action>
```

| Action | Command | Description |
| --- | --- | --- |
| **Crash Client** | `/troll <player> crash` | Sends a deeply nested JSON translate packet that overloads the client's chat renderer, causing it to freeze or crash entirely. |
| **Fake Ban** | `/troll <player> fake_ban` | Kicks the player with a highly convincing ban screen showing a configurable reason and appeal URL, while blocking reconnection for a set number of seconds. |
| **Fake Lag** | `/troll <player> fake_lag` | Simulates rubber-banding by periodically teleporting the player back to a previous position, randomly spikes their displayed ping, and causes block breaks/attacks to fail. |
| **Freeze** | `/troll <player> freeze` | Cancels all movement and teleport events for the target player, locking them firmly in place until toggled off. |
| **Random TP** | `/troll <player> random_tp` | Teleports the player to 5 random locations within a configurable radius over the span of 5 seconds. |
| **Spam Sounds** | `/troll <player> spam_sounds` | Plays a random game sound effect at the player's exact location every few ticks at a completely randomized pitch. |

---

## 🔒 Permissions

To prevent abuse, access to the plugin is tightly controlled.

* **Required Permission Node:** `trollplayer.use`
* Only players or groups with this explicit permission node (or server operators) can execute `/troll` commands.

---

## 🛠️ Requirements & Installation

### Requirements

* **Server Software:** Paper 1.21 or newer
* **Java Version:** Java 21 or higher

### Installation

1. **Download** the latest stable release (`LynceusTroll-1.1.2.jar`).
2. Place the `.jar` file into your server's `plugins/` directory.
3. **Restart** your server to generate the configuration files.
4. (Optional) Customize the plugin behavior by editing the configuration file located at:

```text
   plugins\TrollPlayer\config.yml
```

5. Assign the `trollplayer.use` permission to your admin/staff groups via your permission manager (e.g., LuckPerms).
