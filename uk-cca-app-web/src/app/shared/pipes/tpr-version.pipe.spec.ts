import { TprVersionPipe } from './tpr-version.pipe';

const pipe = new TprVersionPipe();

describe('TprVersionPipe', () => {
  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should display correct TPR version', () => {
    expect(pipe.transform('TP6', 1)).toEqual('TP6-V1');
  });
});
