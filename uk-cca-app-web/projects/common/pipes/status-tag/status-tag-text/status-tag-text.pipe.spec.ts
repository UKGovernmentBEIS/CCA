import { TaskStatusTagMap } from '../status-tag.providers';
import { StatusTagTextPipe } from './status-tag-text.pipe';

describe('StatusTagTextPipe', () => {
  it('create an instance', () => {
    const map: TaskStatusTagMap = { COMPLETED: { text: 'COMPLETED', color: 'blue' } };
    const pipe = new StatusTagTextPipe(map);
    expect(pipe).toBeTruthy();
  });
});
