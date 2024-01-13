Simple mod that uses  `.minecraft/config/custom_ores.json` which contains list of objects `{name: <string>, biome_tag: <string>}` to register custom ores.

For example if you want to add gold block ore in the nether then the config would look like this: 
```
[{"name": "custom_ores:gold_block_ore", "biome_tag": "minecraft:is_nether"}]
```
Then you need to add a datapack (you can for example use kubejs), which contains something like stuff here: https://fabricmc.net/wiki/tutorial:ores, and it should add your ore to the game.
