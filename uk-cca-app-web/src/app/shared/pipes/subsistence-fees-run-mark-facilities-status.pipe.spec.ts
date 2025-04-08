import { SubsistenceFeesRunMarkFacilitiesStatusPipe } from './subsistence-fees-run-mark-facilities-status.pipe';

describe('SubsistenceFeesRunMarkFacilitiesStatusPipe', () => {
  const pipe = new SubsistenceFeesRunMarkFacilitiesStatusPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should display correct subsistence fees run mark facilities status', () => {
    expect(pipe.transform('in_progress')).toEqual('In progress');
    expect(pipe.transform('completed')).toEqual('Completed');
    expect(pipe.transform('cancelled')).toEqual('Cancelled');
  });
});
