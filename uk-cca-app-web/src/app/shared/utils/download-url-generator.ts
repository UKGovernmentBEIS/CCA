export const generateDownloadUrl = (taskId: string) => {
  return `/tasks/${taskId}/file-download/`;
};
