import { RequestActionDTO } from 'cca-api';

import { ItemActionHeaderPipe } from './item-action-header.pipe';

describe('ItemActionHeaderPipe', () => {
  let pipe: ItemActionHeaderPipe;

  const baseRequestAction: Omit<RequestActionDTO, 'type'> = {
    id: 1,
    payload: {},
    submitter: 'John Bolt',
    creationDate: '2021-03-29T12:26:36.000Z',
  };

  beforeAll(() => (pipe = new ItemActionHeaderPipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should display the submitter of the request action', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED',
      }),
    ).toEqual('Target unit account submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'UNDERLYING_AGREEMENT_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Underlying agreement application submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'ADMIN_TERMINATION_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Admin termination submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'ADMIN_TERMINATION_WITHDRAW_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Admin termination withdrawn submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'ADMIN_TERMINATION_FINAL_DECISION_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Admin termination final decision submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Underlying agreement variation application submitted by John Bolt');
  });

  it('should display the approved application title', () => {
    expect(pipe.transform({})).toEqual('Approved Application');
  });
});
