import { mockAccountAction, mockAccountPayload, mockFacility } from '../testing/mock-data';
import { findHistoricalFacility } from './facility-details.resolver';

describe('historical facility identity', () => {
  const facilities = [
    mockFacility({ targetPeriod: 'TP7' }),
    mockFacility({ targetPeriod: 'TP8', version: 4 }),
    mockFacility({ facilityBusinessId: 'AIC-F00065', targetPeriod: 'TP8' }),
  ];
  const action = mockAccountAction(mockAccountPayload({ facilities }));

  it('matches action, business ID and raw target period without selecting the first entry', () => {
    const match = findHistoricalFacility(action, '3293', 'AIC-F00064', 'TP8');
    expect(match).toMatchObject({
      actionId: 3293,
      creationDate: action.creationDate,
      runId: 'BOS-TP8004',
      facility: { ...facilities[1], buyOutCost: '38' },
    });
    expect(findHistoricalFacility(action, '3293', 'AIC-F00065', 'TP8').facility).toMatchObject(facilities[2]);
  });

  it.each([
    ['3294', 'AIC-F00064', 'TP8'],
    ['3293', 'unknown', 'TP8'],
    ['3293', 'AIC-F00064', '8'],
    ['3293', 'AIC-F00064', 'TP9'],
    [null, 'AIC-F00064', 'TP8'],
    ['3293', null, 'TP8'],
    ['3293', 'AIC-F00064', null],
  ])('rejects an unmatched identity %s / %s / %s', (actionId, facilityId, targetPeriod) => {
    expect(findHistoricalFacility(action, actionId, facilityId, targetPeriod)).toBeUndefined();
  });

  it('rejects duplicate historical identities instead of taking the first match', () => {
    const duplicate = mockAccountAction(mockAccountPayload({ facilities: [facilities[0], { ...facilities[0] }] }));
    expect(findHistoricalFacility(duplicate, '3293', 'AIC-F00064', 'TP7')).toBeUndefined();
  });

  it('rejects incompatible actions and payloads', () => {
    expect(
      findHistoricalFacility(
        { ...action, type: 'TP6_SURPLUS_ACCOUNT_PROCESSING_SUBMITTED' },
        '3293',
        'AIC-F00064',
        'TP7',
      ),
    ).toBeUndefined();
    expect(
      findHistoricalFacility({ ...action, payload: { payloadType: 'OTHER_PAYLOAD' } }, '3293', 'AIC-F00064', 'TP7'),
    ).toBeUndefined();
    expect(findHistoricalFacility(null, '3293', 'AIC-F00064', 'TP7')).toBeUndefined();
  });

  it.each(['SUBMITTED', 'OUTSTANDING'] as const)('rejects non-final %s results', (performanceOutcome) => {
    const pending = mockAccountAction(mockAccountPayload({ facilities: [mockFacility({ performanceOutcome })] }));
    expect(findHistoricalFacility(pending, '3293', 'AIC-F00064', 'TP7')).toBeUndefined();
  });
});
