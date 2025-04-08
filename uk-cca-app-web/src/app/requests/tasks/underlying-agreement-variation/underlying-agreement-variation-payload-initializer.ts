import { staticSections, TaskItemStatus, UNAVariationRequestTaskPayload } from '@requests/common';
import { produce } from 'immer';

export function initializeUnderlyingAgreementVariationSubmitPayload(
  currentPayload: UNAVariationRequestTaskPayload,
): UNAVariationRequestTaskPayload {
  return produce(currentPayload, (payload) => {
    // We need to pre-populate the review sections as APPROVED, so that only the changes
    // applied from the sector user's variation can change them to UNDECIDED.
    staticSections.forEach((section) => {
      payload.sectionsCompleted[section] = !payload.sectionsCompleted[section]
        ? TaskItemStatus.COMPLETED
        : payload.sectionsCompleted[section];

      if (!payload.reviewSectionsCompleted[section]) {
        payload.reviewSectionsCompleted[section] = 'APPROVED';
      }
    });

    payload.originalUnderlyingAgreementContainer.underlyingAgreement.facilities.forEach((f) => {
      // pre-populate reviewSectionsCompleted for facilities
      if (!payload.reviewSectionsCompleted[f.facilityId]) payload.reviewSectionsCompleted[f.facilityId] = 'APPROVED';

      payload.sectionsCompleted[f.facilityId] = !payload.sectionsCompleted[f.facilityId]
        ? TaskItemStatus.COMPLETED
        : payload.sectionsCompleted[f.facilityId];
    });
  });
}
