import { ParamMap } from '@angular/router';

import { PageSize } from '@shared/types';

import { HistoricalFacility } from './historical-facilities';

export type FacilityTargetPeriod = HistoricalFacility['targetPeriod'];
export type FacilityResult = Extract<HistoricalFacility['performanceOutcome'], 'TARGET_MET' | 'TARGET_NOT_MET'>;

export interface FacilitiesListFilters {
  readonly search: string;
  readonly targetPeriod: FacilityTargetPeriod | null;
  readonly result: FacilityResult | null;
}

export function defaultFacilitiesListFilters(): FacilitiesListFilters {
  return { search: '', targetPeriod: null, result: null };
}

export function toFacilitiesListFilters(
  queryParamMap: ParamMap,
  targetPeriods: readonly FacilityTargetPeriod[],
): FacilitiesListFilters {
  const result = queryParamMap.get('result');
  return {
    search: queryParamMap.get('search') ?? '',
    targetPeriod: targetPeriods.find((period) => period === queryParamMap.get('targetPeriod')) ?? null,
    result: result === 'TARGET_MET' || result === 'TARGET_NOT_MET' ? result : null,
  };
}

export function availableTargetPeriods(facilities: readonly HistoricalFacility[]): FacilityTargetPeriod[] {
  return [...new Set(facilities.map(({ targetPeriod }) => targetPeriod))].sort((left, right) =>
    left.localeCompare(right, 'en', { numeric: true }),
  );
}

/** Select historical entries without reading, rounding or reconstructing financial values. */
export function filterAndSortFacilities(
  facilities: readonly HistoricalFacility[],
  filters: FacilitiesListFilters,
): HistoricalFacility[] {
  const search = filters.search.trim().toLocaleLowerCase('en-GB');
  return facilities
    .filter(
      (facility) =>
        (!search ||
          facility.facilityBusinessId.toLocaleLowerCase('en-GB').includes(search) ||
          facility.siteName.toLocaleLowerCase('en-GB').includes(search)) &&
        (!filters.targetPeriod || facility.targetPeriod === filters.targetPeriod) &&
        (!filters.result || facility.performanceOutcome === filters.result),
    )
    .sort(
      (left, right) =>
        left.facilityBusinessId.localeCompare(right.facilityBusinessId, 'en', { numeric: true }) ||
        left.targetPeriod.localeCompare(right.targetPeriod, 'en', { numeric: true }),
    );
}

/** Slice already filtered/sorted entries; changing pages does not repeat selection work. */
export function getFacilitiesListPage(
  facilities: readonly HistoricalFacility[],
  requestedPage: number,
  pageSize: PageSize,
) {
  const totalItems = facilities.length;
  const totalPages = Math.max(1, Math.ceil(totalItems / pageSize));
  const currentPage = Math.min(totalPages, Math.max(1, Number.isSafeInteger(requestedPage) ? requestedPage : 1));
  return {
    entries: facilities.slice((currentPage - 1) * pageSize, currentPage * pageSize),
    totalItems,
    currentPage,
    pageSize,
  };
}
