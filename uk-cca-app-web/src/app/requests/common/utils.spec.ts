import { alphabeticalCompare } from './utils';

describe('alphabeticalCompare', () => {
  it('should sort strings using en-GB numeric case-insensitive ordering', () => {
    const values = ['WF-010', 'wf-002', 'WF-001'];

    expect(values.sort(alphabeticalCompare)).toEqual(['WF-001', 'wf-002', 'WF-010']);
  });
});
