type TextMatcher = string | RegExp;
type QueryRoot = Document | Element;

const collapseWhitespace = (value: string | null | undefined): string => (value ?? '').replace(/\s+/g, ' ').trim();

const matchesText = (element: Element, matcher: TextMatcher): boolean => {
  const textContent = collapseWhitespace(element.textContent);
  if (typeof matcher === 'string') {
    return textContent === collapseWhitespace(matcher);
  }

  return matcher.test(textContent);
};

const throwNotFound = (selector: string): never => {
  throw new Error(`Element not found: ${selector}`);
};

const queryAll = (root: QueryRoot, selector: string): HTMLElement[] => {
  return Array.from(root.querySelectorAll<HTMLElement>(selector));
};

export const queryByTestId = (testId: string, root: QueryRoot = document): HTMLElement | null => {
  return root.querySelector<HTMLElement>(`[data-testid="${testId}"]`);
};

export const getByTestId = (testId: string, root: QueryRoot = document): HTMLElement => {
  return queryByTestId(testId, root) ?? throwNotFound(`[data-testid="${testId}"]`);
};

export const getAllByTestId = (testId: string, root: QueryRoot = document): HTMLElement[] => {
  const matches = queryAll(root, `[data-testid="${testId}"]`);
  if (matches.length === 0) {
    throwNotFound(`[data-testid="${testId}"]`);
  }

  return matches;
};

export const queryByText = (matcher: TextMatcher, root: QueryRoot = document): HTMLElement | null => {
  return queryAll(root, '*').find((element) => matchesText(element, matcher)) ?? null;
};

export const getByText = (matcher: TextMatcher, root: QueryRoot = document): HTMLElement => {
  return queryByText(matcher, root) ?? throwNotFound(`text=${String(matcher)}`);
};

export const getAllByText = (matcher: TextMatcher, root: QueryRoot = document): HTMLElement[] => {
  const matches = queryAll(root, '*').filter((element) => matchesText(element, matcher));
  if (matches.length === 0) {
    throwNotFound(`text=${String(matcher)}`);
  }

  return matches;
};

const byId = (id: string, root: QueryRoot): HTMLElement | null => {
  // Use exact id matching to support ids containing dots (e.g. "address.line1").
  if (root instanceof Document) {
    return root.getElementById(id);
  }

  return root.querySelector<HTMLElement>(`[id="${id}"]`);
};

export const queryByLabelText = (matcher: TextMatcher, root: QueryRoot = document): HTMLElement | null => {
  const labels = queryAll(root, 'label');

  for (const label of labels) {
    if (!matchesText(label, matcher)) {
      continue;
    }

    const forId = label.getAttribute('for');
    if (forId) {
      const direct = byId(forId, root);
      if (direct) {
        return direct;
      }
    }

    const nested = label.querySelector<HTMLElement>('input,textarea,select');
    if (nested) {
      return nested;
    }
  }

  return null;
};

export const getByLabelText = (matcher: TextMatcher, root: QueryRoot = document): HTMLElement => {
  return queryByLabelText(matcher, root) ?? throwNotFound(`label=${String(matcher)}`);
};

type RoleOptions = {
  name?: TextMatcher;
};

const roleSelector = (role: string): string => {
  const nativeRoleSelectors: Record<string, string> = {
    button: 'button,[role="button"]',
    link: 'a,[role="link"]',
    heading: 'h1,h2,h3,h4,h5,h6,[role="heading"]',
    row: 'tr,[role="row"]',
    cell: 'td,th,[role="cell"]',
    checkbox: 'input[type="checkbox"],[role="checkbox"]',
    radio: 'input[type="radio"],[role="radio"]',
    textbox: 'input:not([type]),input[type="text"],textarea,[role="textbox"]',
    div: 'div,[role="div"]',
  };

  return nativeRoleSelectors[role] ?? `[role="${role}"]`;
};

const matchesValue = (value: string, matcher: TextMatcher): boolean => {
  const normalizedValue = collapseWhitespace(value);
  if (typeof matcher === 'string') {
    return normalizedValue === collapseWhitespace(matcher);
  }

  return matcher.test(normalizedValue);
};

const matchesRoleName = (element: HTMLElement, name: TextMatcher | undefined): boolean => {
  if (!name) {
    return true;
  }

  const label = element.getAttribute('aria-label');
  if (label && matchesValue(label, name)) {
    return true;
  }

  return matchesText(element, name);
};

export const queryByRole = (
  role: string,
  options: RoleOptions = {},
  root: QueryRoot = document,
): HTMLElement | null => {
  const candidates = queryAll(root, roleSelector(role));
  return candidates.find((element) => matchesRoleName(element, options.name)) ?? null;
};

export const getByRole = (role: string, options: RoleOptions = {}, root: QueryRoot = document): HTMLElement => {
  return queryByRole(role, options, root) ?? throwNotFound(`role=${role}`);
};

export const getAllByRole = (role: string, options: RoleOptions = {}, root: QueryRoot = document): HTMLElement[] => {
  const matches = queryAll(root, roleSelector(role)).filter((element) => matchesRoleName(element, options.name));
  if (matches.length === 0) {
    throwNotFound(`role=${role}`);
  }

  return matches;
};

export const within = (root: QueryRoot) => ({
  getByText: (matcher: TextMatcher) => getByText(matcher, root),
  queryByText: (matcher: TextMatcher) => queryByText(matcher, root),
  getAllByText: (matcher: TextMatcher) => getAllByText(matcher, root),
  getByLabelText: (matcher: TextMatcher) => getByLabelText(matcher, root),
  queryByLabelText: (matcher: TextMatcher) => queryByLabelText(matcher, root),
  getByRole: (role: string, options: RoleOptions = {}) => getByRole(role, options, root),
  queryByRole: (role: string, options: RoleOptions = {}) => queryByRole(role, options, root),
  getByTestId: (testId: string) => getByTestId(testId, root),
  queryByTestId: (testId: string) => queryByTestId(testId, root),
  getAllByTestId: (testId: string) => getAllByTestId(testId, root),
  getAllByRole: (role: string, options: RoleOptions = {}) => getAllByRole(role, options, root),
});

/**
 * Interaction Helpers - Native DOM event dispatchers
 */

/**
 * Simulates a click on an element
 */
export const click = (element: HTMLElement): void => {
  element.dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true }));
};

/**
 * Simulates typing text into an input element
 * Dispatches input and change events after setting the value
 */
export const type = (element: HTMLInputElement | HTMLTextAreaElement, text: string): void => {
  element.focus();
  element.value += text;
  element.dispatchEvent(new InputEvent('input', { bubbles: true, cancelable: true }));
  element.dispatchEvent(new Event('change', { bubbles: true }));
  element.blur();
};

/**
 * Sets the value of an input element
 * Useful for setting initial values or replacing existing text
 */
export const setInputValue = (element: HTMLInputElement | HTMLTextAreaElement, value: string): void => {
  element.focus();
  element.value = value;
  element.dispatchEvent(new InputEvent('input', { bubbles: true, cancelable: true }));
  element.dispatchEvent(new Event('change', { bubbles: true }));
  element.blur();
};

/**
 * Clears the value of an input element
 * Dispatches input and change events after clearing
 */
export const clear = (element: HTMLInputElement | HTMLTextAreaElement): void => {
  setInputValue(element, '');
};

/**
 * GDS Utility - Extracts data from definition lists (<dl> elements)
 *
 * Returns an array of [terms, definitions] pairs for each <dl> element found.
 * Filters out any "Change" links commonly found in GDS summary lists.
 *
 * @param root - Optional root element to search within (defaults to document)
 * @returns Array of [terms, definitions] tuples, where each is an array of trimmed text content
 *
 * @example
 * const data = getSummaryListData();
 * expect(data).toEqual([
 *   [['Label 1', 'Label 2'], ['Value 1', 'Value 2']],
 * ]);
 */
export const getSummaryListData = (root: QueryRoot = document): [string[], string[]][] => {
  return Array.from(root.querySelectorAll('dl')).map((el) => [
    Array.from(el.querySelectorAll('dt')).map((dt) => dt.textContent?.trim() ?? ''),
    Array.from(el.querySelectorAll('dd'))
      .filter((dd) => dd.textContent?.trim() !== 'Change')
      .map((dd) => dd.textContent?.trim() ?? ''),
  ]);
};
