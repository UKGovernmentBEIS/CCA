import { UuidFilePair } from '@shared/components';

import { FileInfoDTO } from 'cca-api';

export type DownloadableFile = {
  fileName: string;
  downloadUrl: string;
};

const toFiles = (fileUUIDs: string[], attachments: Record<string, string>): UuidFilePair[] => {
  if (!Array.isArray(fileUUIDs)) return [];

  return fileUUIDs.filter(Boolean).map((uuid) => ({
    uuid,
    file: { name: attachments[uuid] } as File,
  }));
};

const toUUIDs = (files: UuidFilePair[]): string[] => {
  if (!Array.isArray(files)) return [];

  return files.filter(Boolean).map(({ uuid }) => uuid);
};

const toAttachments = (files: UuidFilePair[]): Record<string, string> => {
  if (!Array.isArray(files)) return {};

  return files.filter(Boolean).reduce<Record<string, string>>((map, { uuid, file }) => {
    map[uuid] = file.name;
    return map;
  }, {});
};

const toDownloadableFiles = (attachments: Record<string, string>, downloadUrl: string): DownloadableFile[] => {
  if (!attachments || typeof attachments !== 'object') return [];

  return Object.entries(attachments).map(([uuid, fileName]) => ({
    fileName,
    downloadUrl: `${downloadUrl}/${uuid}`,
  }));
};

const toDownloadableFromInfoDTO = (files: FileInfoDTO[], downloadUrl: string, prefix = ''): DownloadableFile[] => {
  if (!Array.isArray(files)) return [];

  const validFiles = files.filter(Boolean);

  return validFiles.map(({ uuid, name }) => ({
    fileName: name,
    downloadUrl: `${downloadUrl}${prefix}/${uuid}`,
  }));
};

const toDownloadableDocument = (files: FileInfoDTO[], downloadUrl: string): DownloadableFile[] =>
  toDownloadableFromInfoDTO(files, downloadUrl, '/document');

const toDownloadableFileFromInfoDTO = (files: FileInfoDTO[], downloadUrl: string): DownloadableFile[] =>
  toDownloadableFromInfoDTO(files, downloadUrl);

const extractAttachments = (uuids: string[], attachments: Record<string, string>): Record<string, string> => {
  const validUuids = Array.isArray(uuids) ? uuids.filter(Boolean) : [];

  if (validUuids.length === 0) return {};
  if (!attachments || typeof attachments !== 'object') return {};

  return validUuids.reduce<Record<string, string>>((acc, uuid) => {
    const fileName = attachments[uuid];
    if (fileName) acc[uuid] = fileName;

    return acc;
  }, {});
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
