import { TestBed } from '@angular/core/testing';

import { RequestTaskStore } from '@netz/common/store';

import { tasksContent } from '../tasks-content';
import { ETS_ENERGY_FUEL_DETAILS_SUBTASK, getEtsReportingSections } from './ets-reporting-task-content';
import { mockCompletedEtsPayload, mockEtsFuelPayload, mockEtsPayload } from './testing/mock-data';

describe('ETS reporting task list', () => {
  it('should resolve the ETS submit task to its initial task list', () => {
    TestBed.configureTestingModule({
      providers: [{ provide: RequestTaskStore, useValue: { select: () => () => mockEtsPayload() } }],
    });

    const content = TestBed.runInInjectionContext(() => tasksContent.EMISSIONS_TRADING_SCHEME_DATA_SUBMIT());

    expect(content.header).toBe('UK emissions trading scheme (UK ETS) reporting - Submit form');
    expect(content.sections).toEqual([
      {
        title: 'Energy/fuel details',
        tasks: [
          {
            status: 'NOT_STARTED',
            linkText: 'Provide UK ETS fuel amount consumed',
            link: 'ets-reporting/energy-fuel-amount',
          },
        ],
      },
      {
        title: 'Submit report',
        tasks: [{ status: 'CANNOT_START_YET', linkText: 'Submit your UK ETS report', link: '' }],
      },
    ]);
  });

  it('should preserve an in-progress section and keep submission unavailable', () => {
    const sections = getEtsReportingSections(
      mockEtsPayload({
        sectionsCompleted: { [ETS_ENERGY_FUEL_DETAILS_SUBTASK]: 'IN_PROGRESS' },
      }),
    );

    expect(sections[0].tasks[0].status).toBe('IN_PROGRESS');
    expect(sections[1].tasks[0]).toMatchObject({ status: 'CANNOT_START_YET', link: '' });
  });

  it('should enable submission only when energy/fuel details are completed', () => {
    const sections = getEtsReportingSections(mockCompletedEtsPayload());

    expect(sections[0].tasks[0].status).toBe('COMPLETED');
    expect(sections[1].tasks[0]).toMatchObject({ status: 'NOT_STARTED', link: 'ets-reporting/submit' });
  });

  it('should reject a completed flag without valid branch data', () => {
    const sections = getEtsReportingSections(
      mockEtsPayload({ sectionsCompleted: { [ETS_ENERGY_FUEL_DETAILS_SUBTASK]: 'COMPLETED' } }),
    );
    expect(sections[1].tasks[0]).toMatchObject({ status: 'CANNOT_START_YET', link: '' });
  });

  it('should enable a completed fuel branch and reject stale totals', () => {
    const payload = mockEtsFuelPayload();
    payload.sectionsCompleted[ETS_ENERGY_FUEL_DETAILS_SUBTASK] = 'COMPLETED';
    expect(getEtsReportingSections(payload)[1].tasks[0].link).toBe('ets-reporting/submit');
    payload.emissionsTradingSchemeDataInputDetails.energyFuelDetails.totalPrimaryEnergy = '0';
    expect(getEtsReportingSections(payload)[1].tasks[0].link).toBe('');
  });
});
