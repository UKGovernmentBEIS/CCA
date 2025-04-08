import { UuidFilePair } from '@shared/components';

import { FileInfoDTO } from 'cca-api';

export type Attachments = Record<string, string>;
export type DownloadableFile = { fileName: string; downloadUrl: string };

export const transformAttachmentsToFilesWithUUIDs = (fileUUIDs: string[], attachments: Attachments): UuidFilePair[] =>
  fileUUIDs?.map((uuid) => transformAttachmentToFileWithUUID(uuid, attachments));

export const transformAttachmentToFileWithUUID = (uuid: string, attachments: Attachments): UuidFilePair =>
  uuid ? { file: { name: attachments[uuid] } as File, uuid } : null;

export const transformFilesToUUIDsList = (files: UuidFilePair[] | UuidFilePair): string[] | string =>
  Array.isArray(files) ? files?.map((file) => file.uuid) : files?.uuid;

export const transformFilesToAttachments = (files: UuidFilePair[]): Attachments =>
  files.filter(Boolean).reduce((map, file) => {
    map[file.uuid] = file.file.name;
    return map;
  }, {});

export const transformAttachmentsAndFileUUIDsToDownloadableFiles = (
  fileUUIDs: string[],
  attachments: Attachments,
  downloadUrl: string,
): DownloadableFile[] =>
  fileUUIDs?.filter(Boolean).map((uuid) => ({
    fileName: attachments[uuid],
    downloadUrl: `${downloadUrl}/${uuid}`,
  })) || [];

export const transformAttachmentsToDownloadableFiles = (
  attachments: Attachments,
  downloadUrl: string,
): DownloadableFile[] =>
  Object.entries(attachments).map((e) => ({
    fileName: e[1],
    downloadUrl: `${downloadUrl}/${e[0]}`,
  }));

export const transformFileInfoToDownloadableFile = (
  files: FileInfoDTO | FileInfoDTO[],
  downloadUrl: string,
): DownloadableFile[] => {
  if (Array.isArray(files)) {
    return files.map((f) => ({
      fileName: f.name,
      downloadUrl: `${downloadUrl}/document/${f.uuid}`,
    }));
  } else {
    return [
      {
        fileName: files.name,
        downloadUrl: `${downloadUrl}/document/${files.uuid}`,
      },
    ];
  }
};

export const downloadFileInfoDTOFromAttachmentsUrl = (file: FileInfoDTO, downloadUrl: string): DownloadableFile[] => {
  return [
    {
      fileName: file.name,
      downloadUrl: `${downloadUrl}/${file.uuid}`,
    },
  ];
};
