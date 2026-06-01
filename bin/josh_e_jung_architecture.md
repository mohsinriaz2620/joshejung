# Josh-E-Jung: Architecture & OOP Design Document

This document outlines the technical architecture of **Josh-E-Jung**, a 2D fighting game built in JavaFX. It explains the core mechanics, system design, and how the codebase strictly adheres to Object-Oriented Programming (OOP) and **SOLID** design principles.

---

## 1. System Architecture & Core Loop

At its heart, Josh-E-Jung is built on a **Custom Game Engine** utilizing JavaFX's `AnimationTimer`. 

*   **The Game Loop (`GameEngine.java`)**: The engine runs at roughly 60 frames per second. Every frame, it calculates the time elapsed (`dt` or delta time) and calls two primary methods:
    1.  `update(dt)`: Handles all math, physics, AI logic, and input processing.
    2.  `render(GraphicsContext)`: Clears the screen and draws the current frame based on the updated data.
*   **Input Routing**: Keyboard and Mouse events are captured by the main JavaFX `Scene` and routed through the `GameEngine` directly to whichever game state is currently active.

---

## 2. The State Machine Pattern

Instead of having a massive `if-else` block to figure out what screen the player is on, the game uses the **State Design Pattern**.

*   **`GameState` (Abstract Class)**: Defines the blueprint for a game state, forcing subclasses to implement `enter()`, `exit()`, `update()`, `render()`, and input handling methods.
*   **Concrete States**: The game transitions between independent states like `MenuState`, `CharSelectState`, `StoryState`, `FightState`, and `VictoryState`.
*   **Why this is good**: The `GameEngine` doesn't need to know *what* state it is in. It simply calls `currentState.update()` and `currentState.render()`. 

---

## 3. Object-Oriented Programming (OOP) Concepts

The game heavily leverages the four pillars of OOP:

1.  **Encapsulation**: Game data (like a fighter's HP, Spirit, coordinates, and bounding boxes) are kept `private` or `protected` in the `Fighter` class. They are only modified through specific public methods (like `takeDamage()` or `heal()`), preventing other classes from accidentally corrupting a character's state.
2.  **Inheritance**: All 8 characters inherit from the base `Fighter.java` class. They inherit core physics (jumping, gravity, knockback), basic drawing routines, and hit detection, meaning we don't have to rewrite gravity for every character.
3.  **Polymorphism**: The `FightState` doesn't care if it's drawing a `SufiWarrior` or an `IblisFighter`. It holds two variables: `Fighter p1` and `Fighter p2`. When it calls `p1.drawSelf(gc)`, Java automatically figures out which specific character's overridden drawing method to execute.
4.  **Abstraction**: Complex sub-systems are abstracted away behind manager classes. For example, `AudioManager.playSFX("hit.wav")` completely hides the complexity of loading audio files, setting up `MediaPlayer` threads, and managing volume.

---

## 4. Application of SOLID Principles

The codebase was deliberately structured to follow the 5 SOLID principles of software design:

### 1. Single Responsibility Principle (SRP)
*A class should have one, and only one, reason to change.*
*   **Example**: Look at the sub-systems. `SpriteRenderer.java` is *only* responsible for loading and drawing images. `SoundGenerator.java` is *only* responsible for synthesizing `.wav` files. `AIController.java` is *only* responsible for calculating what move the computer should make. No class is a "God Object" trying to do everything.

### 2. Open/Closed Principle (OCP)
*Software entities should be open for extension, but closed for modification.*
*   **Example**: The roster system. If we want to add a 9th fighter (e.g., "Sher Shah"), we do **not** need to modify the `FightState` or the `GameEngine`. We simply create a new class `SherShah extends Fighter`, implement his specific moves in `initMoves()`, and add his ID to `GameConfig.CHARACTER_IDS`. The game automatically accepts the new fighter without altering existing core code.

### 3. Liskov Substitution Principle (LSP)
*Objects of a superclass shall be replaceable with objects of its subclasses without breaking the application.*
*   **Example**: `GameEngine.createFighter(int index)` returns a `Fighter` object. Whether it returns a `NoorJahan` (a lightweight magic user) or `Dev` (a massive slow tank), the `FightState` can treat them exactly the same. Calling `.performAttack()` on any subclass behaves predictably and safely within the physics engine constraints.

### 4. Interface Segregation Principle (ISP)
*No code should be forced to depend on methods it does not use.*
*   **Example**: Instead of putting `castSpell()` and `applyCurse()` in the base `Fighter` class (which would force non-magical characters like the stone giant `Dev` to have empty spell methods), the game uses specific interfaces: `Spellcaster` and `Curseable`. Only characters that actually use magic (like `NoorJahan` and `IblisFighter`) implement these interfaces.

### 5. Dependency Inversion Principle (DIP)
*High-level modules should not depend on low-level modules. Both should depend on abstractions.*
*   **Example**: The `GameEngine` (high-level) relies entirely on the abstract `GameState` class (abstraction). It knows nothing about the specific implementation details of `FightState` or `ShopState` (low-level). Because the engine depends on the abstraction, we were easily able to plug in the brand new `StoryState` without having to rewrite any of the engine's core routing logic.

---

## 5. Technical Highlights

*   **Procedural Rendering**: The game uses procedural Canvas drawing (`GraphicsContext.fillRect`, `fillOval`, `fillPolygon`) mixed with mathematical functions (`Math.sin(bobTimer)`) to create dynamic, breathing animations without needing massive sprite sheets.
*   **Procedural Audio**: `SoundGenerator.java` writes raw byte arrays mathematically to generate sound waves (sine, square, noise) for battle drums and hit effects, ensuring the game functions entirely offline without requiring heavy asset downloads.
*   **Modular Particle System**: `ParticleSystem.java` handles detached visual effects (sparks, floating text, screen shakes) separately from the fighters, ensuring that if a fighter is destroyed, their special effect particles continue to render naturally.
