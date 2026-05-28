import { ItemDTO } from 'cca-api';

import { ItemLinkPipe } from './item-link.pipe';

type DatasetDTO = Pick<ItemDTO, 'requestType' | 'taskType'> & { expectedPath: (string | number)[] };

describe('ItemLinkPipe', () => {
  const pipe = new ItemLinkPipe();

  const taskId = 1;

  const dataSet: DatasetDTO[] = [
    {
      requestType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_APPLICATION_PEER_REVIEW',
      taskType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_APPLICATION_PEER_REVIEW',
      expectedPath: ['/tasks', taskId],
    },
    {
      requestType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_WAIT_FOR_PEER_REVIEW',
      taskType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_WAIT_FOR_PEER_REVIEW',
      expectedPath: ['/tasks', taskId],
    },
    // NULL
    {
      requestType: null,
      taskType: null,
      expectedPath: ['.'],
    },
  ];

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it.each<DatasetDTO>(dataSet)(
    'should map $requestType . $taskType => $expectedPath',
    ({ requestType, taskType, expectedPath }) => {
      expect(
        pipe.transform({
          requestType: requestType,
          taskType: taskType,
          taskId: taskId,
        }),
      ).toEqual(expectedPath);
    },
  );
});
