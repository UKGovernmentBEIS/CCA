import { TaskStatusTagMap } from '../status-tag.providers';
import { StatusTagColorPipe } from './status-tag-color.pipe';

describe('StatusTagColorPipe', () => {
  it('create an instance', () => {
    const map: TaskStatusTagMap = { COMPLETED: { text: 'COMPLETED', color: 'blue' } };
    const pipe = new StatusTagColorPipe(map);
    expect(pipe).toBeTruthy();
  });
});
