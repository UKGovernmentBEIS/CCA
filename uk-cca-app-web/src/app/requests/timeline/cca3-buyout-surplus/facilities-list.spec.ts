import { convertToParamMap } from '@angular/router';

import {
  availableTargetPeriods,
  defaultFacilitiesListFilters,
  filterAndSortFacilities,
  getFacilitiesListPage,
  toFacilitiesListFilters,
} from './facilities-list';
import { HistoricalFacility } from './historical-facilities';

describe('CCA3 historical facility selection', () => {
  const facility = (
    facilityBusinessId: string,
    targetPeriod: HistoricalFacility['targetPeriod'],
    overrides: Partial<HistoricalFacility> = {},
  ): HistoricalFacility => ({
    facilityBusinessId,
    siteName: 'North London Site',
    targetPeriod,
    version: 1,
    submissionType: 'PRIMARY',
    performanceOutcome: 'TARGET_MET',
    ...overrides,
  });

  const entries = Object.freeze([
    Object.freeze(facility('AIC-F00065', 'TP7', { siteName: 'South site', performanceOutcome: 'TARGET_NOT_MET' })),
    Object.freeze(facility('AIC-F00064', 'TP8', { surplusGained: '0.0000000' })),
    Object.freeze(facility('AIC-F00064', 'TP7', { surplusGained: '0' })),
    Object.freeze(facility('AIC-F00066', 'TP9', { performanceOutcome: 'TARGET_NOT_MET', invoicedBuyOutFee: '0' })),
  ]);

  it('reads valid filters from query parameters and defaults missing filters', () => {
    expect(toFacilitiesListFilters(convertToParamMap({}), availableTargetPeriods(entries))).toEqual(
      defaultFacilitiesListFilters(),
    );
    expect(
      toFacilitiesListFilters(
        convertToParamMap({ search: ' North ', targetPeriod: 'TP8', result: 'TARGET_MET' }),
        availableTargetPeriods(entries),
      ),
    ).toEqual({ search: ' North ', targetPeriod: 'TP8', result: 'TARGET_MET' });
  });

  it.each([
    { targetPeriod: 'TP5', result: 'SUBMITTED' },
    { targetPeriod: 'unknown', result: 'unknown' },
  ])('ignores unavailable periods and unsupported results in the URL: %j', (params) => {
    expect(toFacilitiesListFilters(convertToParamMap(params), availableTargetPeriods(entries))).toEqual(
      defaultFacilitiesListFilters(),
    );
  });

  it('L4 sorts by facility then TP and preserves distinct entries for repeated facilities', () => {
    const result = filterAndSortFacilities(entries, defaultFacilitiesListFilters());
    expect(result.map(({ facilityBusinessId, targetPeriod }) => `${facilityBusinessId}/${targetPeriod}`)).toEqual([
      'AIC-F00064/TP7',
      'AIC-F00064/TP8',
      'AIC-F00065/TP7',
      'AIC-F00066/TP9',
    ]);
    expect(entries.map(({ targetPeriod }) => targetPeriod)).toEqual(['TP7', 'TP8', 'TP7', 'TP9']);
  });

  it('L5/L6 includes all processed TPs oldest first, even with no transactions', () => {
    expect(availableTargetPeriods(entries)).toEqual(['TP7', 'TP8', 'TP9']);
    expect(availableTargetPeriods([entries[0]])).toEqual(['TP7']);
    expect(availableTargetPeriods([])).toEqual([]);
  });

  it.each([
    { search: '  nOrTh LoN  ', expected: ['AIC-F00064', 'AIC-F00064', 'AIC-F00066'] },
    { search: ' f00064 ', expected: ['AIC-F00064', 'AIC-F00064'] },
    { search: 's', expected: ['AIC-F00064', 'AIC-F00064', 'AIC-F00065', 'AIC-F00066'] },
    { search: '   ', expected: ['AIC-F00064', 'AIC-F00064', 'AIC-F00065', 'AIC-F00066'] },
  ])('L7 trims and matches case-insensitive substrings with no minimum: $search', ({ search, expected }) => {
    expect(
      filterAndSortFacilities(entries, { ...defaultFacilitiesListFilters(), search }).map(
        (entry) => entry.facilityBusinessId,
      ),
    ).toEqual(expected);
  });

  it('L7 applies all selected filters with AND', () => {
    expect(filterAndSortFacilities(entries, { search: ' north ', targetPeriod: 'TP8', result: 'TARGET_MET' })).toEqual([
      entries[1],
    ]);
    expect(
      filterAndSortFacilities(entries, { search: ' north ', targetPeriod: 'TP8', result: 'TARGET_NOT_MET' }),
    ).toEqual([]);
    expect(filterAndSortFacilities(entries, { ...defaultFacilitiesListFilters(), targetPeriod: 'TP7' })).toEqual([
      entries[2],
      entries[0],
    ]);
  });

  it('L8 returns an empty page when no entries match', () => {
    const filtered = filterAndSortFacilities(entries, { ...defaultFacilitiesListFilters(), search: 'missing site' });
    expect(getFacilitiesListPage(filtered, 3, 10)).toEqual({
      entries: [],
      totalItems: 0,
      currentPage: 1,
      pageSize: 10,
    });
  });

  it('L9 paginates without losing or repeating entries and clamps a stale page', () => {
    const many = Array.from({ length: 23 }, (_, index) =>
      facility(`AIC-F${String(index + 1).padStart(5, '0')}`, 'TP7'),
    );
    const first = getFacilitiesListPage(many, 1, 10);
    const second = getFacilitiesListPage(many, 2, 10);
    const third = getFacilitiesListPage(many, 8, 10);
    expect([first.entries.length, second.entries.length, third.entries.length]).toEqual([10, 10, 3]);
    expect(third.currentPage).toBe(3);
    expect([...first.entries, ...second.entries, ...third.entries]).toEqual(many);
    expect(getFacilitiesListPage(many, NaN, 10).currentPage).toBe(1);
  });

  it('C5 preserves supplied decimal strings and missing values without interpreting financials or submission type', () => {
    const result = filterAndSortFacilities(entries, defaultFacilitiesListFilters());
    expect(result[0]).toBe(entries[2]);
    expect(result[0].surplusGained).toBe('0');
    expect(result[1].surplusGained).toBe('0.0000000');
    expect(result[2].surplusGained).toBeUndefined();
    expect(result[3].invoicedBuyOutFee).toBe('0');
  });
});
