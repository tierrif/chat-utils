# ChatMacros - Copy messages with a click

This extremely simple mod is meant to be used as a utility for server staff or even normal players to be able to copy chat messages, with or without color codes (experimental). For now, only Fabric 1.16.1 is supported. MaLiLib is required. 

## How to add it to your client
- Go to the releases page for this repository and choose the latest stable release (*or the pre-release if you're feeling adventurous*)
- If you didn't yet, install Fabric Loader 1.16.1. Get it from [here](https://fabricmc.net/use/).
- If you didn't have Fabric installed yet, you'll also need the Fabric API. Download the 1.16.1 version from [here](https://www.curseforge.com/minecraft/mc-mods/fabric-api).
- Download [MaLiLib](https://www.curseforge.com/minecraft/mc-mods/malilib) and [Fabric Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu).
- Put Fabric API, MaLiLib, Fabric Mod Menu and ChatMacros' files in the mods folder (by default, %appdata%\\.minecraft\\mods), or if you're in Linux, ~/.minecraft/mods.
- Start Minecraft. You should see a button called "Mods" with the amount. Click on it and see if everything loaded fine.

## How to use it
ChatMacros is a very simple mod, so it's also very simple to use. By default, hovering received messages in chat should show a tooltip with a preview of what is going to be copied. To copy it to clipboard, simply click the message.

## Settings
To change ChatMacros' settings, press ESC if you're in game and click in the mods button (or simply click on the mods button if you're in the main menu), select ChatMacros in the mod list, and click in a small square settings icon, in the top right.

## Known issues
Copying messages with colors will many times fail, or partially fail in certain servers. This is because of the way messages are originally sent in the server's plugin. A fix for this will be worked on later. If the issue repeats too many times, you can easily disable this in the mod's settings.

Sometimes colors aren't displayed 100% correctly. Please use the issues section if you find a fully reproduce-able case.

If you find any other issues, please don't hesitate to report them in the issues section.
