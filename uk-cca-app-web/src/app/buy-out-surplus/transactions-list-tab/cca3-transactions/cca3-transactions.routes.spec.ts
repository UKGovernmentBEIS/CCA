import { canActivateCca3ChangeStatus } from './cca3-transaction-details/cca3-change-status/cca3-change-status.guard';
import { canActivateCca3MarkAllFacilities } from './cca3-transaction-details/cca3-mark-all-facilities/cca3-mark-all-facilities.guard';
import { canDeactivateCca3TransactionDetails } from './cca3-transaction-details/cca3-transaction-details.guard';
import { CCA3_TRANSACTIONS_ROUTES } from './cca3-transactions.routes';

describe('CCA3_TRANSACTIONS_ROUTES', () => {
  const transactionRoute = CCA3_TRANSACTIONS_ROUTES.find((route) => route.path === ':transactionId');

  it('should allow downloading the transaction files from the transaction details route', () => {
    const fileDownloadRoute = transactionRoute?.children?.find((route) => route.path === 'file-download/:uuid');

    expect(fileDownloadRoute?.title).toBe('Your download has started');
    expect(fileDownloadRoute?.loadComponent).toBeTypeOf('function');
  });

  it('should refresh the transaction details after a facility status change', () => {
    expect(transactionRoute?.runGuardsAndResolvers).toBe('always');
  });

  it('should clear the facilities selection when leaving the transaction', () => {
    expect(transactionRoute?.canDeactivate).toEqual([canDeactivateCca3TransactionDetails]);
  });

  it('should only open the change status page when facilities are selected', () => {
    const changeStatusRoute = transactionRoute?.children?.find((route) => route.path === 'change-status');
    const formRoute = changeStatusRoute?.children?.find((route) => route.path === '');
    const confirmationRoute = changeStatusRoute?.children?.find((route) => route.path === 'confirmation');

    expect(formRoute?.canActivate).toEqual([canActivateCca3ChangeStatus]);
    expect(confirmationRoute?.canActivate).toBeUndefined();
  });

  describe.each([
    [
      'all-paid',
      'PAID',
      'Are you sure you want to mark all selected facilities as paid?',
      'All facilities have been marked as paid',
    ],
    [
      'all-refunded',
      'REFUNDED',
      'Are you sure you want to mark all awaiting facilities as refunded?',
      'All facilities have been marked as refunded',
    ],
  ])('the %s routes', (path, markAllStatus, title, confirmationTitle) => {
    const markFacilitiesRoute = transactionRoute?.children?.find((route) => route.path === 'mark-facilities');
    const markAllRoute = markFacilitiesRoute?.children?.find((route) => route.path === path);
    const formRoute = markAllRoute?.children?.find((route) => route.path === '');
    const confirmationRoute = markAllRoute?.children?.find((route) => route.path === 'confirmation');

    it('should pass the status to mark the facilities with to its children', () => {
      expect(markAllRoute?.data).toEqual({ markAllStatus });
    });

    it('should guard the form by the user role and the transaction in the store', () => {
      expect(formRoute?.canActivate).toEqual([canActivateCca3MarkAllFacilities]);
    });

    it('should title the form after its heading and link back to the transaction details', () => {
      expect(formRoute?.title).toBe(title);
      expect(formRoute?.data).toEqual({ breadcrumb: false, backlink: '../../' });
    });

    it('should title the confirmation after its panel without a back link or guard', () => {
      expect(confirmationRoute?.title).toBe(confirmationTitle);
      expect(confirmationRoute?.data).toEqual({ breadcrumb: false, backlink: false });
      expect(confirmationRoute?.canActivate).toBeUndefined();
    });
  });

  it('should allow downloading the uploaded evidence files', () => {
    const evidenceDownloadRoute = transactionRoute?.children?.find(
      (route) => route.path === 'file-evidences-download/:fileType/:uuid',
    );

    expect(evidenceDownloadRoute?.loadComponent).toBeTypeOf('function');
  });
});
