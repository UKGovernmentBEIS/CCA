/** Id of the `<main>` element the GOV.UK page template gives `tabindex="-1"`. */
export const MAIN_CONTENT_ID = 'main-content';

/**
 * Moves keyboard focus to the page's main content region without scrolling.
 * Mirrors the GOV.UK skip link behaviour: the target carries `tabindex="-1"`,
 * so focusing it does not alter the scroll position.
 */
export function focusMainContent(document: Document): void {
  document.getElementById(MAIN_CONTENT_ID)?.focus({ preventScroll: true });
}
