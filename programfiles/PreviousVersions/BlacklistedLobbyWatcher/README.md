# Blacklisted Lobby Watcher

Minecraft 1.8.9 Forge client-side mod.

## Features

- Watches the client-side player/tab list.
- Checks names against a local blacklist.
- Plays a ping once when a blacklisted player appears.
- Shows currently detected blacklisted players in the HUD.
- Shows both the blacklist and currently detected players in a GUI.
- Automatically removes players from the detected list when they disappear.
- Does not send custom packets, commands, chat messages, or HTTP requests.
- Blacklist is saved locally to:
  `config/blacklisted_lobby_watcher.json`

## Controls

- `B`: open the GUI.
- In the GUI:
  - Type a Minecraft username.
  - Click `Add`.
  - Select an entry and click `Remove`.
  - Click `Done` to close.

## Build

Use Java 8.

Run:

    gradlew setupDecompWorkspace
    gradlew build

The built jar will be in:

    build/libs/

For a development environment:

    gradlew setupDecompWorkspace
    gradlew idea

or

    gradlew setupDecompWorkspace
    gradlew eclipse

## Important

The mod only sees players that the Minecraft 1.8.9 client already knows about through
its normal player-info/tab-list data. It does not query the server for extra players.
