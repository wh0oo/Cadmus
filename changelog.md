### Warning: All save data, including claims from 1.20.1-1.20.4 is not compatible with this release. Please be aware that you'll lose this data if you're updating from a previous Minecraft version.

- Added claim area command to claim large areas by specifying a start and end chunk.
- Colorized command outputs.
- Personal claims are now transferred rather than discarded when joining a team.
- Added allowed blocks claim setting for whitelisting blocks that ignore claim rules.
- Added claim settings and permissions for entity explosions, item pickup, and mob griefing.
- Added `allowedBlocks` claim setting for whitelisting specific block types in claims.
- Claims now protect from the following:
  - Pistons pushing blocks into them.
  - Fluids flowing into them.
  - Fire spreading into them.
- Added Xaero's mod compat with claim support. Thank you Abbie5 for their initial work on this!
- Admin claim tool commands now display the name of the team in a tooltip when suggesting the team's UUID.
- Improved claim screen performance.
- Claiming on the claim screen now instantly updates, rather than only updating when closing the screen.
- Claiming can now be done using a selection box in the claim map.

## API Changes

### Warning: This update is completely incompatible with the original API, as it's been entirely rewritten.

- The selected team (either Vanilla or Argonauts) is now chosen based on a defined weight, rather than hardcoded.
- Team IDs are now stored as UUIDs, rather than strings with a type prefix and UUID.
- All claims are now always synced to the client. This allows developers to use the Claim API for accessing client claims directly.
- Refactored the Claim API. It is now only used for claiming, unclaiming, and reading claims. All claim protections have been moved to the new Protection API
- Added a Protection API. This API allows you to register protections such as block breaking, block placing, etc. This allows developers can register their own mod-specific protections.
- Added a Flag API. This API allows you to interact with admin claims and modify their flags. You can also register additional flags here.
- Added the Team API. This replaces the old Team Provider API and allows you to register team types and get team info.
- Added the following events:
  - AddClaimsEvent
  - RemoveClaimsEvent
  - ClearClaimsEvent
  - CreateTeamEvent
  - RemoveTeamEvent
  - TeamChangedEvent
  - AddPlayerToTeamEvent
  - RemovePlayerFromTeamEvent
  - UpdateTeamInfo
