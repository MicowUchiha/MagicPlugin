# Magic Roadmap

This is a *very rough* approximation of where Magic is headed in future versions.

## 7.4

 - Class improvements and additions:
     - Classbound items that are given on unlock and taken away on lock
     - Wand "unstashable tag", sets a generic item tag
     - "Unmoveable" tag, for use with classbound items that go in a specific slot
     - ModifyMage action to lock/unlock/activate classes, for building a class selection GUI
     - Ability to use spellshop without a wand
 - Status effect system for temporary (timed) property changes

## 7.5

 - Ability to apply attribute equations to properties like protection
 - Internal attribute system
   - Including damage reduction
   - Mana bonuses
   - Maybe others, see TODO
   - GUI to view player stats
   - GUI to upgrade attributes
   - Levelling system? 
   - Health, hunger, oxygen, etc modifiers
 
## 7.6

 - First draft of RPG-style configs with a variety of classes to choose from
 - Players may choose one class, and then add more classes as they max out (up to some limit)
 
## 7.7

 - Improved Magic Mobs
   - Probability tables for drops
   - Disguise support for armor stands with equipped items
   - Boss bar support 
   - Scale difficulty based on players (when spawned)
   - Mob factions
   - Additional triggers:
     - entered combat
     - attacked
     - spawned
     - killed player
     - leave combat (??)
     - change target
     - ... explode, teleport - getting very specific now
     - custom signals triggered by spells
 - Persistent effect support
   - Replacing old automata tracking
   - Ability to have spells cast on chunk load and cancel on unload
   - Also support for mob spawners
   - Configured locations with support for adding locations in-game

## 8.0
 
 - 1.13 support
 - Depending on how much the API changes, it seems likely that this version will *not* be backwards compatible!