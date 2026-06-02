JOSH-E-JUNG: TEKKEN EDITION
===========================

Welcome to Josh-E-Jung, a full-fledged JavaFX fighting RPG rooted in Pakistani and Desi Mythology. This project was overhauled to demonstrate advanced Object-Oriented principles, multi-file architecture, game state management, and an impressive AI-generated asset library.

HOW TO RUN:
-----------
Simply double-click the `run_game.bat` file in this directory. 
The script will automatically compile all the necessary `.java` files from the `src` folder, put the compiled classes in the `bin` folder, and launch the game.

GAME MODES:
-----------
- ARCADE MODE: Fight your way through a series of increasingly difficult AI opponents. Earn gold and buy upgrades from the Bazaar between matches.
- STORY MODE: Experience the narrative across 3 epic chapters with cutscenes, battling iconic mythological villains to restore the Amulet of Jamshed.
- VS MODE (PvP): Grab a friend and battle it out on the same keyboard in local multiplayer mode.

ROSTER (8 Fighters):
--------------------
Heroes:
1. Al-Murtaza (The Sufi Warrior) - Balanced fighter with the Zulfiqar sword.
2. Noor Jahan (The Mughal Sorceress) - Magical ranged attacker with curses.
3. Peer Sahib (The Mystic Dervish) - Strong healer with whirling attacks.

Villains:
4. Marid (The Water Djinn) - Tidal attacks with massive knockback.
5. Churail (The Ghost Witch) - Fast attacker with life-draining abilities.
6. Dev (The Stone Giant) - Massive tank with incredible health and slow, devastating blows.
7. Jinn (The Fire Spirit) - Agile combatant with infernal speed.
8. Iblis (The Dark Lord) - The final boss with hellfire rain and apocalyptic moves.

CONTROLS:
---------
MENU / SHOP:
- UP / DOWN ARROWS: Navigate menus.
- ENTER: Select option.

FIGHTING (PLAYER 1):
- W/A/S/D: Move and Jump
- SHIFT: Block
- T: Light Punch
- Y: Heavy Punch
- G: Light Kick
- H: Heavy Kick
- U: Special Attack (Requires Spirit)
- J: Heal (Requires Spirit)

FIGHTING (PLAYER 2 / PvP ONLY):
- UP/DOWN/LEFT/RIGHT ARROWS: Move and Jump
- ENTER: Block
- NUMPAD 7: Light Punch
- NUMPAD 8: Heavy Punch
- NUMPAD 4: Light Kick
- NUMPAD 5: Heavy Kick
- NUMPAD 9: Special Attack

FEATURES INCLUDED:
------------------
1. Modular OOP Architecture: GameEngine, State Machine (MenuState, FightState, etc.), Abstract Fighter class, and distinct systems for Audio, HUD, Combos, and Particles.
2. High-Fidelity Graphics: 14 unique AI-generated assets (8 character portraits, 5 detailed stage backgrounds, title screen) with procedural sprite scaling, caching, and canvas layering.
3. Advanced Combat: Best of 3 rounds, health bars, spirit meters, combo tracking with damage multipliers, blocking, knockbacks, and hit-stun.
4. Cinematic Polish: Hit-stop (slow-mo), screen shake, particle emitters (fire, lightning, sparks), character hit-flashing, and combo announcements.
5. Adaptive AI: 4 difficulty tiers that dynamically read player distance and health to execute aggressive, defensive, or reactive tactics.

Enjoy the battle!
