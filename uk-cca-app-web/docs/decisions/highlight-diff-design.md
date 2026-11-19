# Summary Diff Design

> **Status (2026-09-03):** decided, implementation pending. `HighlightDiffComponent` on `master` still uses the serialized-DOM + `html-diff-ts` pipeline described in “Current Problem” below. The open work is listed in [../open-findings.md](../open-findings.md).

## Purpose

Summary views display the previous and current state of a wizard submission. The diff must make changes easy to identify without mixing text from unrelated values or creating unsafe HTML.

The required text behaviour is deliberately atomic:

- Equal text is rendered once.
- Different text is rendered as one complete removed batch followed by one complete added batch.
- The diff does not split replacements into character or word fragments.

For example, changing `passt` to `past` renders the complete `passt` value as removed and the complete `past` value as added. The same rule applies to complete sentences.

## Current Problem

`HighlightDiffComponent` currently renders two `SummaryComponent` instances, serializes their DOM, and passes the resulting HTML to `html-diff-ts`. The library then compares one large HTML document.

This creates two problems:

1. Text from different summary rows can be matched together.
2. The generated diff markup must be parsed, sanitized, and inserted with `[innerHtml]`.

Sanitizing the final HTML does not correct an incorrect comparison. `Renderer2` would make DOM creation more explicit, but it would not make generated `innerHTML` safe or solve the wrong comparison boundary.

## Diff At The Summary Model Boundary

The summary data already has an ordered structure:

```text
SummaryData
  sections[]
    data[] (summary rows)
      value[]
```

The diff should operate on this structure before rendering rather than on serialized DOM.

No artificial ID is required for the initial implementation. Sections and rows are already iterable, and old and current summaries are produced by the same workflow. The default matching strategy is:

- Match sections by position.
- Align rows within a matched section as ordered sequences. The displayed key can be used as a matching hint when it is unchanged; otherwise use position and surrounding row structure.
- Match array values by position within a matched row.

This makes clear insertions and removals added or removed rows instead of trying to align their text with a neighbouring row. There is an unavoidable ambiguity when a row has no stable identity, its label changes, and another row is inserted or removed nearby. In that case the algorithm should prefer a conservative positional match and report the affected rows as changed rather than pretending to know their identity.

Where a reliable domain key already exists, the implementation may use it as a matching hint. The displayed `key` must not be treated as a permanent identity: a changed label should be able to remain a changed version of the same row when its position and surrounding structure indicate that relationship.

If future summary workflows reorder rows or need unambiguous matching across arbitrary insertions, matching metadata can be added explicitly to the summary factory. That is an enhancement, not a prerequisite for replacing the unsafe HTML diff.

## Structural Diff Model

The diff layer should produce a typed intermediate model. It should not produce HTML strings.

Conceptually:

```ts
type DiffText =
  | { kind: 'unchanged'; value: string }
  | { kind: 'changed'; previous: string; current: string };

type DiffRow =
  | { kind: 'unchanged'; current: SummarySection }
  | { kind: 'changed'; previous: SummarySection; current: SummarySection }
  | { kind: 'removed'; previous: SummarySection }
  | { kind: 'added'; current: SummarySection };
```

The actual model can preserve the existing `SummarySection` union and add equivalent section and value result types. The important property is that each result says whether content is unchanged, changed, removed, or added.

### Row rules

- **Unchanged row:** render the current label and values once.
- **Changed value:** render the label once and pass the old and new text to the value renderer.
- **Changed label:** render the old label and new label as complete removed/added batches. Do not silently discard the old label.
- **Removed row:** render the old row with removed styling.
- **Added row:** render the new row with added styling.
- **Changed row kind:** if a row changes from a normal value to a file list or link list, render the old and new row blocks separately.

Sections follow the same rules. A changed section header is a changed label; an added or removed section keeps its complete contents on its respective side.

## Value Diff Renderer

The value renderer should be a small Angular component or a focused directive with both values as inputs:

```html
<cca-diff-value
  [previous]="previousValue"
  [current]="currentValue"
/>
```

Its behaviour is:

```text
previous === current -> render one normal text value
otherwise           -> render previous as removed, then current as added
```

The renderer must use Angular interpolation or text nodes. It must not use `innerHTML`, `ElementRef.nativeElement.innerHTML`, or a `DomSanitizer` bypass.

`Renderer2` may be used to create `span` elements and text nodes if a directive needs to decorate an existing host, but normal Angular template rendering is preferred because Angular owns the resulting view and updates it safely.

Newlines should be preserved by the value element's existing `pre-line` behaviour. Text values, links, file lists, and actions should remain separate rendering cases; a text diff renderer must not attempt to recreate their markup from strings.

## Labels And Identity

Labels are part of the structural diff, not just decoration. A wizard may change both a row label and its value, or add and remove rows entirely.

The visible label is not a sufficient identity by itself:

- If the label changes, matching by label incorrectly reports a removed row and an added row.
- If a row is genuinely inserted or removed, matching only by position can make later rows look changed.

The initial positional strategy is appropriate because summary rows are generated in a known workflow order. It should be covered by tests for:

- a value-only change;
- a label and value change at the same position;
- an added row;
- a removed row;
- an added or removed section;
- a changed row rendering mode.

If those tests expose workflows with row reordering, introduce optional non-visible matching metadata in `SummaryFactory` (for example, a logical field name). Do not use the rendered HTML or DOM position as a long-term cross-workflow identity.

## Rendering And Accessibility

Removed and added batches must not rely on colour alone. Keep the existing visual treatment, but expose the state to assistive technology with appropriate text or semantics. The exact mechanism should be validated against the GOV.UK accessibility patterns used by the application.

The output should remain normal Angular DOM. Click handling for summary links should continue to operate on Angular-rendered `RouterLink` elements rather than links reconstructed from diff HTML.

## Migration Plan

1. Add pure structural and text diff functions with no Angular or DOM dependencies.
2. Add focused tests for atomic replacement and structural changes.
3. Add an Angular value renderer for unchanged, removed, and added text batches.
4. Extend `SummaryComponent` or add a summary diff renderer that consumes the paired summary model.
5. Replace `HighlightDiffComponent`'s serialized-DOM pipeline.
6. Remove the `html-diff-ts` dependency and the `[innerHtml]` rendering path once all consumers use the new renderer.
7. Update summary snapshots only after behavioural tests establish the intended output.

## Shared Library Boundary

This mechanism may eventually be moved into a shared Angular library. The library should support two levels of integration because not every consuming project has a summary factory or a structured summary model.

### Structured consumers

Projects with a data model can use the structural diff API described above. They can diff sections, rows, and values independently and render changed labels and values with the Angular components supplied by the library.

### Template-only consumers

Projects that currently create complete Angular templates can use a template-level component, but the library must not attempt to infer fields by parsing the resulting HTML. Angular does not expose the semantic relationship between two arbitrary `ng-template` instances after they have been rendered.

The safe generic behaviour for an unstructured template is therefore:

- render the previous template as one complete removed block;
- render the current template as one complete added block; or
- render both templates normally and apply change state to their containing blocks.

This guarantees that content is not mixed, but it cannot highlight individual fields.

To get field-level highlighting, a template-only consumer must opt into structure by marking corresponding values explicitly, for example with a library directive or component that receives both values:

```html
<cca-diff-value [previous]="previousName" [current]="currentName" />
```

The same principle applies to links and other rich content: the consumer keeps the markup in an Angular template and supplies the values or templates that should be paired. The shared library provides rendering and diff semantics; it does not reconstruct arbitrary markup from strings.

The public library API should therefore keep these concerns separate:

1. A framework-independent atomic text diff function.
2. Angular renderers for text batches and complete template blocks.
3. Optional structural helpers for consumers that can provide sections, rows, or explicit field pairs.

This allows existing template-only projects to adopt safe whole-block diffing first, then opt into precise field-level diffing incrementally.

## Decision

Diff the summary data, not the rendered HTML. Use the existing ordered section/row/value structure for matching, represent structural changes explicitly, and render changed text as complete old/new batches through Angular templates. IDs can be introduced later for workflows that prove they need stronger matching, but they should not be invented merely to make the current DOM-based approach work.
