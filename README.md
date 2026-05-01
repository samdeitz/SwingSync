# 🔥 SwingSync

**A lightning-fast, UI Hot-Reloader for Java Swing.**

SwingSync fundamentally changes how you build Java Swing applications. Instead of restarting your entire application every time you tweak a button's color or adjust a layout constraint, SwingSync watches your file system, dynamically intercepts the JVM's memory Metaspace, and rebuilds your active UI component in real-time.

![Demo](https://github.com/user-attachments/assets/f123338c-3e81-41c8-b83b-48c41e6e8f1d)


## ✨ Features
* **Zero-Restart UI Development:** Save your `.java` file and watch the screen update instantly.
* **Deep State Preservation:** Edits to your UI don't destroy your backend data. If you are connected to a server or holding active game states, those memory pointers are perfectly preserved across the hot-swap.
* **Frictionless Integration:** No complex configurations. Wrap your `JFrame` and `JPanel` in one line of code and start building.
* **Smart Dependency Cascading:** Edit a deeply nested child component, and SwingSync automatically forces the root panel to rebuild and fetch the newest versions of all its children.

---

## 🚀 Quick Start Guide

### 1. Installation

**Manual Upload**
1. Go to the [Releases page](./releases) and download the latest `swingsync-1.0.jar`.
2. Drop the `.jar` file into a `lib` folder inside your Java project.
3. Add the `.jar` to your IDE's referenced libraries (In VS Code, open the "Java Projects" sidebar, find "Referenced Libraries", click the `+`, and select the jar).

**Gradle**

1. Add JitPack Repo to your build file
'''
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
''' 
2. Add SwingSync as a dependency

'''
dependencies {
        implementation 'com.github.samdeitz:SwingSync:Tag'
}
'''

### 2. The Golden Rule of SwingSync
SwingSync uses a custom orphan `ClassLoader` to bypass Java's default memory cache. For this to work safely, **your UI classes must be inside a named package.** Do not use the "default" (unnamed) package at the root of your `src` folder.

### 3. Implementation
Initialize your application exactly as you normally would, then hand your `JFrame` and any state variables over to the `SwingSyncer`. 

```java
import javax.swing.JFrame;
import SwingSync.SwingSyncer;
import backend.PlayerState;
import ui.MainLobby;

public class AppLauncher {
    public static void main(String[] args) {
        JFrame frame = new JFrame("My Application");
        frame.setSize(1200, 700);
        
        // 1. Initialize your dynamic data
        String roomName = "Room Alpha";
        PlayerState state = new PlayerState();

        // 2. Set up the initial screen
        MainLobby lobby = new MainLobby(roomName, state);

        // 3. Attach the Hot-Reloader!
        // Pass the frame, panel, followed by the EXACT variables your UI constructor (JPanel) needs.
        // SwingSync will save these pointers and reuse them every time the UI rebuilds.
        Syncher syncher = new Syncher(frame, lobby, roomName, state); // SwingSync will add your panel to your frame
        syncher.start() // start thread for reloading

        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}
