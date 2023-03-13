# XP Font
XP Font is a Spigot/Paper plugin for minecraft 1.16 that provides a simple way for each player on a server to store and retrieve XP levels.

May work for newer versions, but is currently untested and not being maintained at the moment.

## Usage
By default, an emerald block is used as the accessor block for the back-end xp storage. 
Right click the emerald block with a book to turn it into an xp font.

Right click to store a level, shift + right click to store all experience and levels.

Left click to retrieve a level, and shift + left click to retrieve all experience and levels from storage.

XP is stored for each player in either a serialized or YAML format.
- Serialized stores all font block and xp storage data in a non-editable .xpfont file.
- YAML stores font block data in fontblocks.yml, and xp storage in playerfonts.yml. Both are editable.
- SQL support planned, but currently defaults back to YAML if chosen.

## Commands
Many of these commands have additional aliases.
- xpcheck: Display xp stored in your xp font
- xpdeposit: Remotely deposit xp into your font.
- xpwithdraw: Remotely withdraw xp from your font.
- xpadd: Adds xp to the user's font (can also subtract)
- xpset: Sets user's stored xp to the specified amount
- xpmigrate: (Not yet implemented) Migrate all xp data between storage types.

## Permissions
- `xpfont.*` - All non-admin permissions, including remote commands.
- `xpfont.create` - Create new XP Fonts (does not prevent accessing existing fonts).
- `xpfont.destroy` - Destroy/unbind xp fonts (does not prevent the block from being mined).
- `xpfont.deposit` - Store xp into a font.
- `xpfont.withdraw` - Retrieve stored xp from a font.
- `xpfont.check` - View stored xp/level information
- `xpfont.register` - Allow creating xp font data for user
- `xpfont.remote.*` - All remote non-admin commands
- `xpfont.remote.check` - Remotely check stored xp/levels
- `xpfont.remote.check.others` - [Admin/OP] Remotely check any player's stored xp/levels.
- `xpfont.remote.deposit` - Remotely deposit xp into storage
- `xpfont.remote.withdraw` - Remotely withdraw xp from storage
- `xpfont.admin.*` - [Admin/OP] All admin permissions
- `xpfont.admin.addxp` - [Admin/OP] Add/Subtract xp from any player's xp font
- `xpfont.admin.setxp` - [Admin/OP] Set xp in player's xp font to specified level
- `xpfont.admin.migrate` - [Admin/OP] Migrate xp font data storage
