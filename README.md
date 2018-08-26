I made this mod for my friend who likes to play on peaceful difficulty. So now we can play together on server with mobs.

This mod performs exactly two things:
1. Every hostile mob will ignore specified players.
2. Also every hostile mob will run away from these players. Like creepers run away from ocelots. It can be disabled in the config.

There are two commands:
* /tiger_add <player_name> -- add player to the list
* /tiger_remove <player_name> -- remove player from the list

Default config:
```
# Configuration file

general {
    B:fearEveryone=true
    D:avoidDistance=8.0
    D:farSpeedIn=1.0
    D:nearSpeedIn=1.2
    S:tigerUuids <

     >
}

```

This mod is [licensed under the **MIT license**](https://github.com/ilya-pirogov/tigerkus/blob/master/LICENSE)
