import { UuidFilePair } from '@shared/components';

import { FileInfoDTO } from 'cca-api';

export type DownloadableFile = { fileName: string; downloadUrl: string };

const toFiles = (fileUUIDs: string[], attachments: Record<string, string>): UuidFilePair[] =>
  fileUUIDs?.filter(Boolean).map((uuid) => ({ file: { name: attachments[uuid] } as File, uuid })) || [];

const toUUIDs = (files: UuidFilePair[]): string[] =>
  Array.isArray(files) ? files?.filter(Boolean).map((file) => file.uuid) : [];

const toAttachments = (files: UuidFilePair[]): Record<string, string> =>
  !Array.isArray(files)
    ? {}
    : files.filter(Boolean).reduce((map, file) => {
        map[file.uuid] = file.file.name;
        return map;
      }, {});

const toDownloadableFiles = (attachments: Record<string, string>, downloadUrl: string): DownloadableFile[] => {
  if (typeof attachments !== 'object' || !attachments) return [];
  return Object.entries(attachments).map(([key, value]) => ({
    fileName: value,
    downloadUrl: `${downloadUrl}/${key}`,
  }));
};

const toDownloadableDocument = (files: FileInfoDTO[], downloadUrl: string): DownloadableFile[] => {
  if (!Array.isArray(files) || files.filter(Boolean).length === 0) return [];
  return files.filter(Boolean).map((f) => ({
    fileName: f.name,
    downloadUrl: `${downloadUrl}/document/${f.uuid}`,
  }));
};

/**
 * Extracts attachments based on the provided UUIDs.
 * @param uuids - Array of UUIDs to extract.
 * @param attachments - Record of attachments where keys are UUIDs and values are attachment names.
 * @returns A new record containing only the attachments that match the provided UUIDs.
 */
const extractAttachments = (uuids: string[], attachments: Record<string, string>): Record<string, string> => {
  uuids = uuids?.filter(Boolean) || [];
  // Validate inputs
  if (uuids.length === 0) {
    return {};
  }
  // Ensure attachments is an object and has the expected structure
  if (!attachments || typeof attachments !== 'object') {
    return {};
  }
  return uuids.reduce((acc, uuid) => {
    if (attachments[uuid]) {
      acc[uuid] = attachments[uuid];
    }
    return acc;
  }, {});
};

const toDownloadableFileFromInfoDTO = (files: FileInfoDTO[], downloadUrl: string): DownloadableFile[] => {
  if (!Array.isArray(files) || files.filter(Boolean).length === 0) return [];
  return files.filter(Boolean).map((file) => ({
    fileName: file.name,
    downloadUrl: `${downloadUrl}/${file.uuid}`,
  }));
};

export const fileUtils = {
  toFiles,
  toUUIDs,
  toAttachments,
  toDownloadableFiles,
  toDownloadableDocument,
  extractAttachments,
  toDownloadableFileFromInfoDTO,
};
