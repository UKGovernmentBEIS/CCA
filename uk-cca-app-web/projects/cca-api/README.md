# CCAApi

This library contains the OpenAPI generator output based on the [swagger.yaml](src/assets/swagger.yaml) file.

> **Do not edit anything under `src/lib` by hand** — it is regenerated from the swagger file. Any necessary customization lives in the mustache templates under `src/assets/templates`.

## Usage

Import any part of the generated code from the root barrel. E.g.

```typescript
import { SomeService, SomeDTO } from 'cca-api';
```

## Code generation

Run from the repo root (backend must be running on `localhost:8082`):

```bash
yarn generate:api     # fetches the latest spec into src/assets/swagger.yaml, cleans and regenerates src/lib
yarn postgenerate:api # lint-fix, build, format
```

Then build and test the app. There should be few to no problems unless we know beforehand that there will be breaking changes. If more changes are needed, proceed to fix the app.

Commit your working changes using a relevant message, for example `chore(api): generation`.

Full documentation: [`docs/openapi-generator-update-guide.md`](../../docs/openapi-generator-update-guide.md).
