# Harmony Real Users & Couples Architecture

Date: 2026-09-01
Status: Approved design draft awaiting final review before implementation

## 1. Goal

Convert Harmony from a mostly local/simulated couple experience into a real two-user application backed by Supabase Auth and couple-aware cloud data.

The first implementation phase establishes the production foundation for:

- real authenticated users
- solo use before pairing
- two-user couple pairing by code
- couple-aware profiles and data ownership
- secure future partner-answer reveals
- account reset, unpairing, and deletion semantics
- removal of Harmony Brain from active app behavior
- removal of anonymous Supabase auth pollution

The Panda game is explicitly out of scope for this phase and must remain behaviorally unchanged.

## 2. Product principles

1. A newly registered user can use and explore Harmony before connecting a partner.
2. Pairing is optional at first, but the entire production app becomes couple-aware.
3. A couple contains exactly two authenticated users.
4. A user can have at most one active couple connection at a time.
5. A partner response may reveal only after both authenticated couple members have answered the same round.
6. The app may expose the status “partner has answered” without exposing the hidden answer.
7. Future result screens show both users’ profile pictures and names with their answers.
8. Account deletion automatically breaks the couple connection.
9. Harmony reset keeps the login but removes the active couple connection and clears the user’s Harmony progress plus the shared relationship history defined by the reset flow.
10. Harmony Brain is frozen and excluded from production behavior until a later project.

## 3. Scope boundaries

### Included now

- central auth/session state using the existing Supabase Auth login
- persistent user profile tied to `auth.users.id`
- solo mode after login
- partner connection UI and backend
- generated invite codes
- entering invite codes
- exactly two couple members
- active couple state throughout the app
- production-safe logout/account switching behavior
- account reset semantics
- account deletion + automatic unpairing
- Harmony Brain shutdown in the Android app
- removal of anonymous Brain-auth creation
- cleanup of existing anonymous Supabase auth users after the anonymous-creation path is disabled
- Harmony-design account/pairing/reset screens
- data-model foundation for future secure two-user answer reveal

### Explicitly excluded now

- modifying the Panda game
- migrating Panda to two-device remote answering
- enabling Harmony Brain or AI personalization
- advanced multi-partner/group relationships
- more than two members per couple
- rebuilding every existing game interaction in this phase
- chat/fotos/moments full cloud migration unless required by the account/couple safety boundary

## 4. Authentication and app session

The app already supports Supabase Auth for Google and email login. That remains the only identity source.

After login, the app loads an `AppSession` containing at minimum:

- `userId`
- `email` when available
- `profile`
- `activeCoupleId` nullable
- `partnerProfile` nullable
- `pairingState`

The local `isAuthenticated` boolean must no longer be the authoritative identity boundary. It may be used as presentation state only if derived from the actual Supabase session.

### Solo mode

A user with no active couple is fully authenticated and can inspect the app.

UI that requires a partner should show a clear partner-connect call to action instead of simulator data.

The existing production-facing hardcoded partner simulator and hardcoded invite code must not represent a real relationship.

## 5. User profiles

Create a server-backed profile keyed by the authenticated user id.

Recommended fields:

- `user_id uuid primary key references auth.users(id)`
- `display_name text`
- `avatar_path/text`
- `created_at`
- `updated_at`

Profile data belongs to the authenticated user. RLS must allow a user to update only their own profile.

Partner display data may be read only when the requesting user shares an active couple with that partner.

Local Room profile data becomes cache/presentation state, not global identity state.

## 6. Couple model

Recommended tables:

### `couples`

- `id uuid primary key`
- `status` (`active`, `ended`, `reset` as needed)
- `created_at`
- `ended_at nullable`

### `couple_members`

- `couple_id uuid`
- `user_id uuid`
- `joined_at`
- unique active membership per user
- maximum two active members per couple enforced server-side

The database must enforce:

- no third member
- no user in two active couples simultaneously
- only members can read their active couple

## 7. Partner invite flow

The account/profile area exposes a visible **Partner verbinden** action for users with no active couple.

The flow offers two equal actions:

### Code erstellen

The backend generates a short, human-friendly, single-use invite code.

Recommended properties:

- cryptographically random
- short enough to type manually
- case-insensitive
- single-use
- expires after a reasonable period
- tied to the creating user
- invalid once the creator becomes paired

The screen displays the code in Harmony styling and provides a native share action.

Suggested share text contains only the code and a short instruction to log into Harmony and choose “Partner verbinden”.

### Code eingeben

The second authenticated user enters the code after logging into Harmony.

A server-side transaction/RPC must validate:

- invite exists
- invite is active and not expired
- inviter is not the same user
- inviter is still unpaired
- accepting user is unpaired
- couple does not already exist for the invite

On success the server atomically:

1. creates the couple
2. inserts exactly two couple members
3. consumes the invite
4. returns the new couple id

Both apps then refresh their couple session state.

## 8. Re-pairing and unpairing

A user cannot connect to another partner while an active couple exists.

To connect someone else, the current couple must first be ended through an explicit user action.

This avoids ambiguous relationship ownership and accidental crossover of historical data.

## 9. Account actions

The account area distinguishes three actions.

### A. Partner trennen

- keeps Supabase login
- ends the active couple
- user becomes unpaired
- the other member also becomes unpaired
- historical shared data is not silently exposed to future partners
- historical retention policy may archive data for later product decisions, but no future partner inherits it

### B. Harmony zurücksetzen

Approved semantics:

- keeps Supabase login/account
- ends the active couple connection
- removes the user’s Harmony progress
- removes/clears the shared relationship history covered by the reset operation
- clears local cached couple data
- returns both users to an unpaired state where applicable

The UI must warn that the operation is irreversible.

### C. Konto löschen

- deletes the authenticated user account
- automatically ends the active couple before or during deletion
- surviving partner becomes unpaired
- local cached data for the deleted user is cleared
- the deleted account cannot be recovered

The server must derive the user id from the authenticated JWT and never trust a client-supplied user id for deletion.

## 10. Shared-data ownership on deletion

Deletion must distinguish between:

- personal data belonging solely to the deleting user
- shared couple records

The account deletion process must not leave an active couple pointing at a deleted user.

Shared data retention/deletion should be implemented explicitly per data type. No shared record may become visible to a future partner merely because the surviving user re-pairs.

## 11. Local Room safety

Current Room data is not sufficiently separated by authenticated user.

Before real multi-user production use, account switching must not expose the previous user’s cached data.

Initial safe strategy:

- treat cloud/Supabase identity as authoritative
- on logout/account change, clear user/couple-sensitive Room caches that are not safely owner-scoped
- later, owner-scope retained offline tables with `ownerUserId` and where appropriate `coupleId`

No second logged-in user on the same Android device may see the previous user’s answers, chat, memories, profile, or couple data.

## 12. Future secure answer architecture

This phase lays the foundation but does not migrate Panda.

Recommended future entities:

### `question_rounds`

Identifies a specific couple + game + question round.

### `question_answers`

One row per authenticated user per round.

Must enforce unique `(round_id, user_id)`.

### `question_progress`

Contains reveal-safe status such as whether each member has answered, without exposing answer values.

### Secure reveal function/RPC

The client must not be able to select a partner’s hidden answer directly before both users answered.

The reveal function verifies:

- requester is a member of the couple
- round belongs to the couple
- both distinct couple members submitted an answer

Only then does it return both answers.

Future reveal UI must show:

- own profile image/name
- partner profile image/name
- both answer values
- match/difference presentation appropriate to the game

A future optional “both tapped Ergebnis ansehen” state may add synchronized reveal suspense, but the security boundary is always “both have answered”.

## 13. Harmony Brain freeze

Harmony Brain is removed from active production behavior for this project.

Must disable/remove from app execution paths:

- automatic Brain answer analysis
- automatic game generation
- Brain chat/suggestions UI exposure where applicable
- Brain notifications
- anonymous Brain auth token creation
- calls that require the Brain-specific anonymous session helper

Existing backend tables/functions may remain dormant so the feature can be resumed later. Do not destroy useful historical schema solely to hide the feature.

The app must use the real logged-in Supabase session for future authenticated backend behavior.

## 14. Anonymous Supabase users

The current Brain auth helper creates anonymous Supabase users independently from the real Harmony login.

Cleanup order is mandatory:

1. disable/remove the anonymous account creation path in Android
2. verify no production request recreates anonymous users
3. inspect foreign-key dependencies
4. remove existing anonymous auth users
5. verify the remaining auth population contains only intended real users/system-required identities

Do not delete anonymous users first while the creation path is still active.

## 15. Harmony UI design

Pairing/account screens should use the existing Harmony/Aurora visual language rather than copying reference screenshots literally.

### Unpaired account state

Show:

- both-person/couple visual motif
- “Noch nicht verbunden”
- explanation that Harmony can be explored solo
- primary action: “Partner verbinden”

### Partner connect screen

Show two large actions:

- “Code erstellen”
- “Code eingeben”

### Generated code screen

Show:

- prominent short code
- expiry text
- “Code teilen”
- copy action
- cancel/revoke action where appropriate

### Connected state

Show both profile images and both display names with a clear connected status.

### Account reset/delete screens

Use Harmony styling with stronger destructive emphasis, confirmation controls, and concise irreversible-action explanations.

## 16. Security / RLS requirements

Every new user/couple table must have RLS enabled.

Minimum rules:

- users update only their own profile
- users read only their own profile plus active partner display fields needed by the UI
- only active couple members can read couple data
- a client cannot directly insert arbitrary couple memberships
- invite acceptance occurs through a controlled server-side function/transaction
- no client may insert rows pretending to be another `user_id`
- all user ids are validated against `auth.uid()`
- account lifecycle operations use authenticated server-side functions

## 17. Error handling

Pairing must have explicit user-facing states for:

- invalid code
- expired code
- already used code
- inviter already paired
- accepting user already paired
- self-invite attempt
- network/offline failure

Pairing transactions must be atomic so partial couples cannot be created.

Account reset/delete must be idempotent enough to recover from interrupted client requests without leaving half-active membership state.

## 18. Testing requirements

Before release, test with at least two real non-anonymous Supabase accounts and two independent app sessions/devices.

Required scenarios:

1. User A registers/logs in and explores solo.
2. User B registers/logs in and explores solo.
3. A generates code; B accepts it.
4. Both apps resolve the same couple id and correct partner profile.
5. A cannot generate a second active couple while paired.
6. B cannot join another couple while paired.
7. A logs out; B’s data is not visible when a different account logs into A’s device.
8. Partner disconnect makes both unpaired.
9. Harmony reset keeps login but clears relationship/progress state as specified.
10. Account deletion automatically unpairs the survivor.
11. Anonymous user count does not grow after Brain freeze.
12. Existing Panda game behavior remains unchanged.

## 19. Implementation order

1. Freeze Harmony Brain execution paths and anonymous Brain auth.
2. Establish authoritative app session/user identity.
3. Add cloud profiles.
4. Add couple/member/invite schema + RLS + atomic pairing RPCs.
5. Add pairing/account UI in Harmony design.
6. Add safe logout/account switching cache behavior.
7. Add reset/unpair/delete lifecycle backend operations.
8. Remove existing anonymous auth users only after anonymous creation is verified disabled.
9. Add the future question-round/reveal data foundation if it can be done without modifying Panda behavior.
10. Verify with two real accounts/two sessions and run security checks.

## 20. Acceptance criteria

The foundation is complete when:

- a real authenticated user can use Harmony while unpaired
- two real authenticated users can connect using a generated code
- a couple cannot contain more than two users
- a user cannot belong to two active couples
- both users see correct profile names/images after pairing
- logout/account switching cannot leak previous local user data
- reset keeps login but removes couple + Harmony relationship/progress state
- deleting an account automatically unpairs the surviving partner
- Harmony Brain does not execute in the production app
- no new anonymous Supabase auth users are generated by Harmony Brain
- the existing anonymous users are cleaned up after dependency verification
- Panda behavior remains unchanged
- the architecture is ready for later secure two-user answer reveal with profile pictures
