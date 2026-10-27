/**
 * Builds the value for an `aria-describedby` attribute from the ids of the elements that describe a
 * control. Ids that are absent are skipped, so the control only references descriptions that are
 * actually rendered. Returns `null` when there is nothing to describe the control.
 */
export function describedBy(...ids: (string | null | undefined | false)[]): string | null {
  const presentIds = ids.filter((id): id is string => typeof id === 'string' && id.length > 0);
  return presentIds.length > 0 ? presentIds.join(' ') : null;
}
