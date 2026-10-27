import {
  BuyOutSurplusAccountProcessingRequestMetadata,
  BuyOutSurplusFacilityAccountProcessingRequestMetadata,
  RequestDetailsDTO,
} from 'cca-api';

const CCA2_BUYOUT = 'BUY_OUT_SURPLUS_ACCOUNT_PROCESSING';
const CCA3_BUYOUT = 'BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING';

/** One visible history filter searches both generations at the API boundary. */
export function expandBuyoutHistoryTypes(requestTypes: readonly string[]): string[] {
  return [...new Set(requestTypes.flatMap((type) => (type === CCA2_BUYOUT ? [type, CCA3_BUYOUT] : [type])))];
}

type HistoryMetadata = {
  initiatorRoleType?: string;
  cca2?: BuyOutSurplusAccountProcessingRequestMetadata & { transactionId?: string };
  cca3?: { buyOutTransactionIds: string[]; refundTransactionIds: string[] };
};

function transactionIds(codes: Record<string, string> | undefined): string[] {
  return Object.entries(codes ?? {})
    .sort(([left], [right]) => left.localeCompare(right, 'en', { numeric: true }))
    .map(([, code]) => code)
    .filter((code) => typeof code === 'string' && code.trim().length > 0);
}

export function toHistoryMetadata(workflow: RequestDetailsDTO): HistoryMetadata {
  if (workflow.requestType === CCA3_BUYOUT) {
    const metadata = workflow.requestMetadata as BuyOutSurplusFacilityAccountProcessingRequestMetadata | undefined;
    return {
      cca3: {
        buyOutTransactionIds: transactionIds(metadata?.buyOutTransactionCodes),
        refundTransactionIds: transactionIds(metadata?.refundTransactionCodes),
      },
    };
  }

  if (workflow.requestType === CCA2_BUYOUT) {
    const metadata = workflow.requestMetadata as BuyOutSurplusAccountProcessingRequestMetadata | undefined;
    // Retain legacy history responses while also accepting the generated CCA2 contract.
    const legacyId = metadata && 'transactionId' in metadata ? metadata.transactionId : undefined;
    return {
      cca2: {
        ...metadata,
        transactionId: typeof legacyId === 'string' ? legacyId : metadata?.transactionCode,
      },
    };
  }

  const metadata = workflow.requestMetadata;
  return {
    initiatorRoleType:
      metadata && 'initiatorRoleType' in metadata && typeof metadata.initiatorRoleType === 'string'
        ? metadata.initiatorRoleType
        : undefined,
  };
}
