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

  it('should return the payments', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PAYMENT_MARKED_AS_PAID',
      }),
    ).toEqual('Payment marked as paid by John Bolt (BACS)');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PAYMENT_CANCELLED',
      }),
    ).toEqual('Payment task cancelled by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PAYMENT_MARKED_AS_RECEIVED',
      }),
    ).toEqual('Payment marked as received by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PAYMENT_COMPLETED',
      }),
    ).toEqual('Payment confirmed via GOV.UK pay');
  });

  it('should display the approved application title', () => {
    expect(pipe.transform({})).toEqual('Approved Application');
  });
});
