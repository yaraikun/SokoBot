# SokoBot 🤖 - An Intelligent Warehouse Keeper

**SokoBot** is an artificial intelligence project. It solves the classic
Japanese puzzle game, Sokoban (倉庫番). This program thinks, plans, and finds a
path to clean the warehouse.

---

### 📜 Project Description

This project is an AI agent that can solve Sokoban puzzles. The project's core
is a search algorithm that explores possible moves. It avoids dead-end
situations and finds a solution. The project is built in Java with the Swing
framework for the GUI. It is managed with Gradle.

This was developed as a major course output for a course on Artificial
Intelligence. It focused on informed search algorithms, state representation,
and heuristic design.

---

### 💻 Tech Stack

*   **Language:** Java
*   **GUI:** Java Swing
*   **Build Tool:** Gradle

---

### 🚀 Getting Started

Follow these instructions to get the project running on your local machine.

#### Prerequisites

*   **Java Development Kit (JDK)**: Version 21.
*   **Git**: For cloning the repository.

#### Installation & Running

1.  **Clone the repository:**
    ```sh
    git clone <your-repository-url>
    cd Sokobot
    ```

2.  **Build the project using Gradle:**
    The Gradle wrapper included in the project will automatically download the
    correct Gradle version.

    ```sh
    ./gradlew build
    ```
    >*(On Windows, use `gradlew.bat build`)*

3.  **Run the application.**
    The application requires two arguments: the level name without `.txt` and
    the mode (`fp` or `bot`).

    *   **To run in Free Play Mode 🧑‍💻:**
        ```sh
        ./gradlew run --args='testlevel fp'
        ```

    *   **To let the SokoBot solve it 🤖:**
        ```sh
        ./gradlew run --args='fourboxes1 bot'
        ```
        > After the window loads, press **Space** to start the bot's thinking
        > process.

---

### ✍️ Commit Message Convention

This project follows the Conventional Commits specification to maintain a clean
git history. Each commit message has a **type**, a **scope** (optional), and a
**subject**.

```
<type>(<scope>): <subject>
```

Here is a table of the most common types:

| Type | Description | Example |
| :--- | :--- | :--- |
| **`feat`** | A new feature for the user. | `feat: Implement A* search algorithm` |
| **`fix`** | A bug fix for the user. | `fix: Prevent crates from pushing through walls`|
| **`docs`** | Changes to the documentation (e.g., `README.md`). | `docs: Add setup instructions to README` |
| **`style`**| Code style changes that do not affect meaning. | `style: Format SokoBot.java with Prettier` |
| **`refactor`**| A code change that neither fixes a bug nor adds a feature. | `refactor: Extract state representation to new class` |
| **`test`** | Adding missing tests or correcting existing tests. | `test: Add unit test for deadlock detection` |
| **`chore`**| General maintenance or updating dependencies. | `chore: Update Gradle version to 9.0` |
| **`build`**| Changes that affect the build system or external dependencies. | `build: Configure working directory in Gradle` |

---

### 📁 Project Structure

```
Sokobot/
├── .gitignore
├── build.gradle
├── settings.gradle
├── README.md
└── sokobot/
    ├── maps/              # Contains all the .txt level files
    └── src/
        ├── graphics/      # Image assets for the game
        ├── gui/           # All Java Swing GUI components
        ├── main/          # The main Driver class
        ├── reader/        # File reader for parsing map files
        └── solver/        # 🧠 The solver logic is here (SokoBot.java)
```

---

Made with ❤️ and lots of coffee.
