import { TargetUnitAccountDetailsResponseDTO } from 'cca-api';

type TargetUnitBreadcrumbData = { targetUnit: TargetUnitAccountDetailsResponseDTO };

/** Breadcrumb of the target unit page. */
export const targetUnitBreadcrumb = ({ targetUnit }: TargetUnitBreadcrumbData) =>
  `${targetUnit.targetUnitAccountDetails.name}`;

/**
 * Breadcrumb with the same text as the target unit breadcrumb, so it merges into it and makes the target unit
 * crumb open the given tab.
 */
export const targetUnitTabBreadcrumb = (fragment: string) => (data: TargetUnitBreadcrumbData) => ({
  text: targetUnitBreadcrumb(data),
  fragment,
});
