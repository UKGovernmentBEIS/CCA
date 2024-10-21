### SIDE EFFECTS:

Side effects contains a helper class [SideEffectsHandler](./side-effects-handler.ts) for applying task-wide side effects.  
It can be injected in a request task's routes and used (provided) throughout the subtask.  
[SideEffectsHandler](./side-effects-handler.ts) requires the provision of at least one [SIDE_EFFECT](./side-effects.providers.ts) injection token of type [SideEffect](./side-effect.ts).

_Example:_

_subtask-side-effect.ts_

```typescript
import { SideEffect } from './side-effect';
import { StateSelector } from './signal-store';

export class SubtaskASideEffect extends SideEffect {
  override subtask = 'SubtaskA';

  override apply(): void {
    let subtaskSelector: StateSelector<any, any>;
    const subtaskState = this.store.select(subtaskSelector)();
    // ... apply actual side effect to state ...
  }
}

export class SubtaskBSideEffect extends SideEffect {
  override subtask = 'SubtaskB';

  override apply(): void {
    let subtaskSelector: StateSelector<any, any>;
    const subtaskState = this.store.select(subtaskSelector)();
    // ... apply actual side effect to state ...
  }
}
```

_tasks.routes.ts_

```typescript
import { Routes } from '@angular/router';
import { SIDE_EFFECTS } from './side-effects.providers';
import { SideEffectsHandler } from './side-effects-handler';

export const ROUTES: Routes = [
  {
    path: 'task-name',
    providers: [
      SideEffectsHandler,
      { provide: SIDE_EFFECTS, multi: true, useClass: SubtaskASideEffect },
      { provide: SIDE_EFFECTS, multi: true, useClass: SubtaskBSideEffect },
    ],
    // ...
  },
  // ...
];
```

_form.component.ts_

```typescript
import { Inject } from '@angular/core';
import { SideEffectsHandler } from './side-effects-handler';

class FormComponent {
  constructor(private sideEffects: SideEffectsHandler) {}

  // ...

  protected submit(): void {
    this.sideEffects.apply('SubtaskA');
    // api call...
  }
}
```
