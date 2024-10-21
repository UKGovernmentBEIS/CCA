import { summaryData, summaryDataNoChangeParam } from './mock';
import { SummaryQueryParamsPipe } from './queryParams.pipe';

describe('Summary Change link Query Params pipe', () => {
  let pipe: SummaryQueryParamsPipe;
  beforeEach(() => {
    pipe = new SummaryQueryParamsPipe();
  });
  it('should always change query param by default', () => {
    summaryData.forEach((s) => {
      s.data.forEach((d) => {
        expect(pipe.transform(d)).toEqual({ change: true });
      });
    });
  });
  it('should not append change query param if explicitly set to', () => {
    summaryDataNoChangeParam.forEach((s) => {
      s.data.forEach((d) => {
        if (d.appendChangeParam === false) {
          expect(pipe.transform(d)).toEqual({});
        } else {
          expect(pipe.transform(d)).toEqual({ change: true });
        }
      });
    });
  });
});
