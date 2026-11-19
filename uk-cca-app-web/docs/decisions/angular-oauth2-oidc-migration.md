# angular-oauth2-oidc Migration Assessment

> **Date:** 2026-09-03 (pruned 2026-09-22)
> **Context:** the `keycloak-js` adapter is on the current release (26.2.4, against a 26.4.0 server). The open
> question is whether to replace it with `angular-oauth2-oidc`. The work items the migration implies are listed
> in [../open-findings.md](../open-findings.md); the research and the plan behind them are below.

## 1. Current adapter and the seam

`KeycloakService` is the only place the adapter is instantiated — a facade (`init`, `login`, `createLoginUrl`,
`logout`, `updateToken`, `loadUserProfile`, `isTokenExpired`, `token`, `tokenParsed`, `refreshTokenParsed`,
`keycloakEvents` signal). Any library swap stays behind it.

| File | Usage |
|------|-------|
| `src/app/shared/services/keycloak.service.ts` | The **only** instantiation site — the facade above. |
| `src/app/shared/services/auth.service.ts` | User-facing wrapper (`login`, `createLoginUrl`, `logout`, `loadUserProfile`, `loadIsLoggedIn`). |
| `src/app/interceptors/keycloak-bearer.interceptor.ts` | `updateToken(minValidity)` then `Authorization: Bearer`. |
| `src/app/timeout/timeout-banner/timeout-banner.service.ts` | Timeout banner driven by the **refresh** token's `exp`/`iat`. |
| `src/app/forgot-password/submit-otp/submit-otp.component.ts` | Builds a sign-in URL for display after password reset (async since 26.x). |
| `src/app/app.config.ts` | `APP_INITIALIZER`: builds the config from `environment` plus the API-provided `keycloakServerUrl`, then `keycloakService.init()`. |
| `src/environments/environment.ts`, `environment.prod.ts` | `keycloakConfig` (realm `uk-pmrv`, clientId `uk-cca-web-app`) + `keycloakInitOptions` (`onLoad: 'check-sso'`, `pkceMethod: 'S256'`, `enableLogging`). |
| `projects/common/auth/auth.state.ts`, `auth.selectors.ts`, `auth.store.ts` | `KeycloakProfile` **type leak** into the shared lib (type-only, no runtime dependency) — rework it as part of the migration. |
| Specs | `keycloak.service.spec.ts`, `auth.service.spec.ts`, `submit-otp.component.spec.ts`, `timeout-banner.service.spec.ts`, `guards/mocks.ts` (shared `mockKeycloakService`), `app.component.spec.ts`, `landing-page.component.spec.ts`, `authorize.guard.spec.ts` |

## 2. Build & tooling facts

- **Pure ESM and dependency-free** (`"type": "module"`, ESM-only `exports`; dropped `js-sha256` and `jwt-decode`). The app builds with `@angular/build:application` (esbuild) — handled natively. `tsconfig.json` already has `"moduleResolution": "bundler"`, which 26.x requires.
- **Secure context:** 26.x requires Web Crypto → HTTPS or `localhost`. Dev on `localhost:4202` is fine; accessing the dev server via LAN IP over plain HTTP will break.
- **Server coordination: none.** Server is already 26.4.0. Keycloak 26.0+ no longer serves `/js/keycloak.js` — irrelevant, the app bundles the npm package. The server's `KC_HTTP_RELATIVE_PATH=/auth` is already handled via the API-provided `keycloakServerUrl`.

## 3. Migration to `angular-oauth2-oidc` — research findings

Researched against primary sources: the [npm registry](https://registry.npmjs.org/angular-oauth2-oidc), the library's [GitHub repository](https://github.com/manfredsteyer/angular-oauth2-oidc) (README, CHANGELOG, `docs-src`, source in `projects/lib/src`), and the [Keycloak documentation](https://www.keycloak.org/securing-apps/javascript-adapter).

### 3.1 Library status & Angular 22 support

| Fact | Value | Source |
|------|-------|--------|
| Latest version on npm (`latest` dist-tag) | **22.0.2** | [npm registry](https://registry.npmjs.org/angular-oauth2-oidc) |
| Published | 2026-06-26 (changelog entry 2026-07-02) | [npm `time`](https://registry.npmjs.org/angular-oauth2-oidc) / [CHANGELOG](https://github.com/manfredsteyer/angular-oauth2-oidc/blob/master/CHANGELOG.md) |
| Licence | MIT | [npm metadata](https://registry.npmjs.org/angular-oauth2-oidc/latest) |
| Runtime deps | only `tslib` | [npm metadata](https://registry.npmjs.org/angular-oauth2-oidc/latest) |
| Package format | ESM only (`fesm2022/*.mjs`) | [npm metadata](https://registry.npmjs.org/angular-oauth2-oidc/latest) |

- The library tracks Angular majors one-to-one: README states *"Successfully tested with Angular 4.3 to Angular 22"* and *"Angular 22: Use 22.x versions of this library"*; 22.0.2 peer-depends on `@angular/core >=22.0.0` — [README](https://github.com/manfredsteyer/angular-oauth2-oidc/blob/master/README.md), [npm metadata](https://registry.npmjs.org/angular-oauth2-oidc/latest). **Use `angular-oauth2-oidc@^22.0.2` with this app's Angular 22.1.4.**

Maintenance activity (npm publish dates from the [registry `time` field](https://registry.npmjs.org/angular-oauth2-oidc), changelog notes from [CHANGELOG](https://github.com/manfredsteyer/angular-oauth2-oidc/blob/master/CHANGELOG.md)):

| Version | npm publish | Notes |
|---------|-------------|-------|
| 21.0.0 → 21.0.2 | 2026-06-08 → 06-11 | Angular 21 line; repaired test infra, added lib unit tests + CI |
| 22.0.0 → 22.0.2 | 2026-06-11 → 06-26 | Angular 22; RxJS 7; bugfix: token-revocation endpoint may return non-JSON |
| 21.0.3 | 2026-06-26 | 22.0.2 fix backported to the 21.x LTS line |

Actively maintained (the Angular 22 line shipped within ~2.5 months and receives bugfixes with LTS backports); ~2k-star repo with 6 maintainers. Integration shape for this standalone app: use `provideOAuthClient()` rather than `OAuthModule.forRoot()` — [README](https://github.com/manfredsteyer/angular-oauth2-oidc/blob/master/README.md).

### 3.2 Keycloak compatibility

- **Code flow + PKCE S256 + OIDC discovery work out of the box.** PKCE is built in (`code_challenge` + `code_challenge_method=S256` whenever `responseType` includes `code` and PKCE isn't disabled) — [`oauth-service.ts` `createLoginUrl`](https://github.com/manfredsteyer/angular-oauth2-oidc/blob/master/projects/lib/src/oauth-service.ts). `loadDiscoveryDocument()` appends `/.well-known/openid-configuration` to the configured `issuer` — so `issuer` must be `{keycloakServerUrl}/realms/{realm}` — [`oauth-service.ts` `loadDiscoveryDocument`](https://github.com/manfredsteyer/angular-oauth2-oidc/blob/master/projects/lib/src/oauth-service.ts).
- **Keycloak is a documented, tested IdP**: README credits Keycloak ("On the server-side we've used IdentityServer ..., Redhat's Keycloak (Java), and Auth0"); the docs contain a dedicated Keycloak page — [README](https://github.com/manfredsteyer/angular-oauth2-oidc/blob/master/README.md), [`docs-src/authsvr-keycloak.md`](https://github.com/manfredsteyer/angular-oauth2-oidc/blob/master/docs-src/authsvr-keycloak.md).
- One Keycloak-relevant caveat: `strictDiscoveryDocumentValidation` defaults to `true` (all discovery endpoints must start with the issuer URL). If Keycloak's frontend/backend URLs differ (common behind a reverse proxy), set it `false` — [`docs-src/discovery-document-validation.md`](https://github.com/manfredsteyer/angular-oauth2-oidc/blob/master/docs-src/discovery-document-validation.md).
- **Session-state iframe:** verified against the server source that Keycloak **26.0.0 still publishes `check_session_iframe`** in its discovery doc ([`OIDCWellKnownProvider.java @ 26.0.0`](https://github.com/keycloak/keycloak/blob/26.0.0/services/src/main/java/org/keycloak/protocol/oidc/OIDCWellKnownProvider.java)). It doesn't matter for refresh anyway: for code flow the library's silent refresh is a plain `grant_type=refresh_token` POST (no iframe); the hidden-iframe `prompt=none` path is used only with `useSilentRefresh: true` or implicit flow — [`oauth-service.ts` `refreshInternal`](https://github.com/manfredsteyer/angular-oauth2-oidc/blob/master/projects/lib/src/oauth-service.ts), [`docs-src/silent-refresh.md`](https://github.com/manfredsteyer/angular-oauth2-oidc/blob/master/docs-src/silent-refresh.md).

### 3.3 API mapping (this app's usage → library equivalent)

All equivalents verified against [`projects/lib/src/oauth-service.ts`](https://github.com/manfredsteyer/angular-oauth2-oidc/blob/master/projects/lib/src/oauth-service.ts), [`auth.config.ts`](https://github.com/manfredsteyer/angular-oauth2-oidc/blob/master/projects/lib/src/auth.config.ts), [`events.ts`](https://github.com/manfredsteyer/angular-oauth2-oidc/blob/master/projects/lib/src/events.ts) and [`types.ts`](https://github.com/manfredsteyer/angular-oauth2-oidc/blob/master/projects/lib/src/types.ts).

| App usage (keycloak-js) | angular-oauth2-oidc equivalent | Notes |
|---|---|---|
| `init(config, { onLoad: 'check-sso', pkceMethod: 'S256' })` | `provideOAuthClient()` + `configure(authConfig)` + `loadDiscoveryDocumentAndTryLogin()` (or `loadDiscoveryDocumentAndLogin()`) | `...AndTryLogin()` loads the discovery doc, then processes a `code` already in the URL with the PKCE verifier. It **never redirects** and never contacts the IdP proactively — see §3.4 (silent SSO). |
| `updateToken(minValidity)` before API calls | `hasValidAccessToken()` + `getAccessTokenExpiration()` + `refreshToken()` | **No built-in min-validity refresh** — the "refresh if expiring within N s" condition must be implemented in the facade (the interceptor's 120s/30s pattern maps onto it). `refreshToken()` POSTs `grant_type=refresh_token`, stores the rotated refresh token, emits `token_received` + `token_refreshed`; failures emit `token_refresh_error` (`OAuthErrorEvent`). |
| Bearer token (`keycloak.token`) | `getAccessToken()` (or `authorizationHeader()` → `"Bearer ..."`) | |
| `login(options)` | `initLoginFlow()` / `initCodeFlow(additionalState, params)` | Redirects via `config.openUri` (default `location.href = uri`). **No per-call `redirectUri` option** — see §3.4 (createLoginUrl / redirectUri). |
| `createLoginUrl(options)` (sync string) | **No public synchronous equivalent** — the lib's `createLoginUrl` is `protected` and `async`; the built URL lives on the public `loginUrl` property | See §3.4 (createLoginUrl / redirectUri). Note: the current adapter already forces this call async, so the component must change either way. |
| `logout({ redirectUri })` | `logOut()` or `revokeTokenAndLogout()` | Clears storage, emits `logout`, redirects to `logoutUrl` (`end_session_endpoint`) with `id_token_hint` + `post_logout_redirect_uri` (from `postLogoutRedirectUri`, falling back to `redirectUri`). `revokeTokenAndLogout()` first revokes tokens at `revocation_endpoint` (RFC 7009). |
| `loadUserProfile()` → `KeycloakProfile` | `loadUserProfile()` → untyped `object` | GET on `userinfo_endpoint`; resolves `{ info: <claims merged with id_token claims> }`; verifies `sub` unless `skipSubjectCheck`. Emits `user_profile_loaded` / `user_profile_load_error`. |
| `isTokenExpired(minValidity)` | `hasValidIdToken()` / `hasValidAccessToken()` / `getAccessTokenExpiration()` | No per-call min-validity parameter; "expires within N s" must be computed from `getAccessTokenExpiration()` (ms since epoch). Validity uses `expires_at - decreaseExpirationBySec > now - clockSkewInSec` (default skew 600 s). |
| Event callbacks (`onAuthSuccess`, `onAuthRefreshSuccess`, `onAuthRefreshError`, `onAuthLogout`, `onTokenExpired`, `onReady`, `onActionUpdate`) | `OAuthService.events: Observable<OAuthEvent>` | See event mapping table below. |
| `refreshTokenParsed` (`.exp` / `.iat`) — drives the timeout banner | **Gap** — only `getRefreshToken()` (raw JWT), `getAccessTokenExpiration()`, `getIdTokenExpiration()` are exposed | See §3.4 (timeout banner). |
| `KeycloakProfile` (type) | No equivalent — lib ships `UserInfo { sub: string; [key: string]: any }` | Replace with a local profile interface in `projects/common` (see §3.4). |
| `keycloakInstance` passthrough | `OAuthService` itself (inject it in the facade) | |

**Event mapping** (full list in [`events.ts`](https://github.com/manfredsteyer/angular-oauth2-oidc/blob/master/projects/lib/src/events.ts)):

| keycloak-js callback (this app) | OAuthEvent type(s) | Notes |
|---|---|---|
| `onAuthSuccess` | `token_received` (fires on every token acquisition, incl. refresh) | also `token_refreshed` |
| `onAuthRefreshSuccess` | `token_refreshed` | emitted by `refreshToken()` after storing new tokens |
| `onAuthRefreshError` | `token_refresh_error` (`OAuthErrorEvent`) | |
| `onAuthLogout` | `logout` (emitted by `logOut()`); `session_terminated` only if `sessionChecksEnabled` | |
| `onTokenExpired` | `token_expires` (info = `'access_token'` or `'id_token'`) | scheduled by internal timers (`timeoutFactor`, default 0.75 of lifetime) |
| `onReady` | `discovery_document_loaded` / `discovery_document_load_error` | init-completion signal |
| `onActionUpdate` (AIA) | none | unused by this app anyway (see §3.4) |

**Concrete `AuthConfig` for this app** (derived from `src/environments/*.ts` and [`auth.config.ts`](https://github.com/manfredsteyer/angular-oauth2-oidc/blob/master/projects/lib/src/auth.config.ts)):

```typescript
const authConfig: AuthConfig = {
  issuer: `${keycloakServerUrl}/realms/uk-pmrv`,  // appends /.well-known/openid-configuration
  clientId: 'uk-cca-web-app',
  redirectUri: location.origin,
  responseType: 'code',          // enables PKCE S256 automatically
  scope: 'openid',               // 'openid' is force-added anyway; Keycloak adds its default scopes
  showDebugInformation: !environment.production,  // replaces enableLogging
  useSilentRefresh: false,       // default for code flow → refresh-token grant (no iframe)
  requireHttps: 'remoteOnly',    // default: http allowed on localhost only
  postLogoutRedirectUri: location.origin,
  sessionChecksEnabled: false,
};
// bootstrap: provideOAuthClient(); then configure(authConfig) + loadDiscoveryDocumentAndTryLogin()
```

### 3.4 Gaps & risks

- 🔴 **`check-sso` equivalence does not exist out of the box — but keycloak-js's own mechanism is now verified from its shipped code.** Verified identical in 25.0.6 (`node_modules/keycloak-js/dist/keycloak.js`) and [26.2.4 `lib/keycloak.js`](https://raw.githubusercontent.com/keycloak/keycloak-js/26.2.4/lib/keycloak.js): keycloak-js `check-sso` = (1) the **invisible session-status iframe** (`login-status-iframe.html`, `display:none`, title `keycloak-session-iframe`) first reports whether the Keycloak session state changed; (2) if the user is not already authenticated, the actual silent sign-in is **`login({ prompt: 'none' })` — a full-page redirect** to the auth endpoint (no login form, but a real top-level navigation that reloads the app); a hidden `keycloak-silent-check-sso` iframe performing the sign-in exists but is used **only when `silentCheckSsoRedirectUri` is configured — which this app does not configure**; (3) with 3rd-party cookies blocked (the default in modern browsers) keycloak-js disables the iframes and check-sso falls back to the plain `prompt=none` redirect.
  - So "silent SSO via an invisible frame" is only half true: the invisible frame *monitors* the session; the sign-in itself is a `prompt=none` redirect.
  - `angular-oauth2-oidc`'s `loadDiscoveryDocumentAndTryLogin()` does **neither** (no iframe pre-check, no `prompt=none` redirect), so users already logged into Keycloak are **not** signed in on first visit. Options: (a) `loadDiscoveryDocumentAndLogin()` — always redirects when no valid tokens; anonymous users are bounced to the Keycloak login page (a `login-required` behaviour change); (b) approximate keycloak-js check-sso manually: on bootstrap with no valid tokens, redirect via `initCodeFlow()` with a `prompt=none` query parameter — the exact same mechanism keycloak-js uses; Keycloak returns a `code` (signed in) or `login_required` error (stay anonymous). Needs spike validation against the lib's parameter handling; (c) `useSilentRefresh: true` iframe emulation — the approach browsers are restricting. **Needs a product decision.**
- 🟡 **`createLoginUrl` / per-call `redirectUri`.** The forgot-password flow builds a sign-in URL for display; the lib's URL builder is `protected` and `async`. Workaround: expose it via the facade (subclass or `silent-refresh.html`-free approach) or build the URL manually from the discovery endpoints. `AuthService.getLoginOptions()` overrides `redirectUri: location.origin` for `blockSignInRedirect` routes — `initCodeFlow()` has no per-call redirect URI, so the facade must temporarily set/restore `oauthService.redirectUri` (or pass `redirect_uri` via `customQueryParams`).
- 🔴 **Refresh-token expiry is not exposed (timeout banner).** The timeout banner and idle logout are driven by the refresh token's `exp`/`iat`. The library exposes `getRefreshToken()` (raw JWT) but no parsed/expiry values. Workaround: decode the refresh-token JWT payload (base64-decode `getRefreshToken().split('.')[1]`) in the facade after `token_received`/`token_refreshed` and re-publish a `refreshTokenParsed` equivalent. Must be re-read after **every** refresh (the stored refresh token is replaced on each successful refresh).
- 🟡 **Refresh-token rotation vs concurrent refreshes.** Keycloak issues single-use refresh tokens by default (confirm the `uk-pmrv` realm's token settings). `angular-oauth2-oidc` handles rotation (stores the returned token) but has **no in-flight refresh dedupe** — the per-request `updateToken` interceptor pattern can trigger concurrent refreshes, and the loser presents an already-consumed token → `400 invalid_grant` → `token_refresh_error` → failed requests/logout. Note: keycloak-js already dedupes this internally via its `refreshQueue` (verified in 25.0.6 `dist/keycloak.js` and [26.2.4 `lib/keycloak.js`](https://raw.githubusercontent.com/keycloak/keycloak-js/26.2.4/lib/keycloak.js)), so the facade must replicate that queue over `refreshToken()` and define a failure policy (retry once after re-login vs force logout).
- 🟡 **Cross-tab / cross-window logout detection.** keycloak-js fires `onAuthLogout` when its session-status iframe detects a logout elsewhere. Here that's the optional `sessionChecksEnabled` feature (OIDC Session Management polling → `session_changed` / `session_terminated`), off by default and limited by third-party-cookie policies. The timeout banner's `OnAuthLogout → idleLogout()` path maps to the `logout` event, with cross-tab detection lost unless session checks are enabled.
- 🟢 **No equivalents, but unused:** `accountManagement()` / `createAccountUrl()`, `register()`, AIA (`onActionUpdate`). Verified not used: the facade only wraps `init/login/createLoginUrl/logout/updateToken/loadUserProfile/isTokenExpired` + events; 2FA goes through the backend. Also, the library implements OIDC RP-Initiated Logout (`end_session_endpoint` + `id_token_hint` + `post_logout_redirect_uri`) — exactly what Keycloak 26 requires (26.0 removed the legacy `redirect_uri` logout parameter) — [Keycloak 26.0.0 upgrading guide](https://raw.githubusercontent.com/keycloak/keycloak/main/docs/documentation/upgrading/topics/changes/changes-26_0_0.adoc).
- 🟢 **`KeycloakProfile` type leak.** `projects/common/auth/{auth.state.ts, auth.selectors.ts, auth.store.ts}` type the profile as `KeycloakProfile`. Replace with a local interface (e.g. `UserProfile` mirroring the profile claims used by the UI) — 3 files, type-only.
- 🟢 **Testing.** `OAuthService` stores tokens in `sessionStorage` by default and uses `HttpClient` for discovery/token/userinfo. In the Vitest/jsdom suite `provideOAuthClient()` + `provideHttpClientTesting()` works, but the facade should remain the only component touching `OAuthService` so the existing `mockKeycloakService`-style mocks in `guards/mocks.ts` keep working unchanged.

### 3.5 Effort estimate for this repo

**24 files affected** (grep for keycloak imports → 23, plus `submit-otp.component.ts` via `AuthService`); **10 of them are specs/mocks**:

| Category | Files |
|---|---|
| Facade (adapter seam) | `src/app/shared/services/keycloak.service.ts` (+ re-export in `services/index.ts`) |
| Consumers | `auth.service.ts`, `interceptors/keycloak-bearer.interceptor.ts`, `timeout/timeout-banner/timeout-banner.service.ts`, `forgot-password/submit-otp/submit-otp.component.ts` |
| Bootstrap/config | `app.config.ts`, `environments/environment.ts` + `environment.prod.ts`, `shared/config/config.state.ts`, `projects/cca-api/src/lib/model/uIPropertiesDTO.ts` (field name only) |
| Type leak | `projects/common/auth/auth.state.ts`, `auth.selectors.ts`, `auth.store.ts` |
| Specs + mocks | `guards/mocks.ts`; `keycloak.service.spec.ts`, `auth.service.spec.ts`, `timeout-banner.service.spec.ts`, `app.component.spec.ts`, `submit-otp.component.spec.ts`, `landing-page.component.spec.ts`, `authorize.guard.spec.ts`, `terms-and-conditions.guard.spec.ts`, `delete-2fa.component.spec.ts` |

**Recommended migration shape — re-implement the `KeycloakService` facade over `OAuthService` and keep its public surface** (the seam this repo already documents):

1. `keycloak.service.ts`: keep all method/property signatures; internally `configure()` + `loadDiscoveryDocumentAndTryLogin()`, map `events` → the `keycloakEvents` signal, add refresh dedupe + min-validity guard, implement `refreshTokenParsed` by decoding `getRefreshToken()`.
2. `app.config.ts`: `APP_INITIALIZER` becomes `provideOAuthClient()` + facade `init(AuthConfig)` (issuer built from the API-provided `keycloakServerUrl`).
3. Environments: replace `keycloakConfig`/`keycloakInitOptions` with `authConfig`-shaped settings.
4. Interceptor: unchanged shape — `updateToken(minValidity)` becomes the facade's conditional refresh (the min-validity logic keycloak-js built in must be implemented here).
5. `timeout-banner.service.ts`: re-map `OnAuthRefreshSuccess` → `token_refreshed`, `OnAuthLogout` → `logout`; read `refreshTokenParsed` from the facade's decoded value.
6. `submit-otp.component.ts`: `createLoginUrl` is async — already required by the current adapter, so no double work.
7. `projects/common`: replace the `KeycloakProfile` import with a local interface.
8. Specs: `keycloak.service.spec.ts` + `timeout-banner.service.spec.ts` are behavioural; the rest are touch-ups; `mocks.ts` stays valid if the facade surface is preserved.

**Sizing: medium effort (~3–5 dev-days including QA)** — the mechanical parts (config, environments, types, specs) are small; the real work is the check-sso decision, the refresh-token-expiry rework for the timeout banner, refresh dedupe, and logout/cross-tab semantics, plus manual QA against the live Keycloak 26.4.0 realm.

## 4. Recommendation

### 4.1 How to proceed

**Recommended:**

1. **Schedule the `angular-oauth2-oidc` migration as a planned effort** (~3–5 dev-days, §3.5). The library itself is healthy: `22.0.2` (MIT, Angular-22-aligned, actively maintained, ESM). The existing `KeycloakService` facade is the seam — the swap stays behind it.
2. **Resolve the silent-SSO product decision before committing** (§3.4). `loadDiscoveryDocumentAndLogin()` bounces anonymous users to the Keycloak login page — a behaviour change from `check-sso`; emulating `check-sso` via iframe silent refresh fights browser cookie restrictions. If silent SSO for pre-authenticated users is a hard requirement, that alone may justify staying on keycloak-js.

**Not recommended:** a migration without that decision — it would change how every pre-authenticated user is greeted, and re-implementing the timeout banner on an unparsed refresh token is the bulk of the work (§3.4).

### 4.2 Decision criteria

| Criterion | Stay on keycloak-js | Migrate to angular-oauth2-oidc |
|---|---|---|
| Silent SSO (`check-sso`) required | ✅ native | ⚠️ lost, or requires a product-approved behaviour change |
| Adapter maintenance | ✅ (26.2.4 current) | ✅ (22.0.2 current) |
| Migration cost | ✅ zero beyond the upgrade | ~3–5 dev-days + QA |
| Timeout banner / refresh-token expiry | ✅ built in | ⚠️ re-implement (decode refresh JWT in facade) |
| Vendor independence / generic OIDC | — | ✅ |

### 4.3 Suggested sequencing
1. **Spike branch**: re-implement the facade over `OAuthService` and verify the flows mapped in §3.3 against the live 26.4.0 realm (sign-in, refresh, logout, forgot-password link, timeout banner).
2. **Confirm the `uk-pmrv` realm's refresh-token settings** (single-use rotation) to size the refresh-dedupe work.
3. **Product decision** on check-sso behaviour; then full migration if approved.
