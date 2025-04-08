package uk.gov.cca.api.common.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.service.ZipFileExtractor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Log4j2
@UtilityClass
public class ZipUtils {

    public byte[] generateZipFile(List<FileDTO> files) {
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ZipOutputStream zipOut = new ZipOutputStream(outputStream)
        ) {
            for(FileDTO file: files) {
                ZipEntry zipEntry = new ZipEntry(file.getFileName());
                zipOut.putNextEntry(zipEntry);
                zipOut.write(file.getFileContent());
                zipOut.closeEntry();
            }

            zipOut.finish();

            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER);
        }
    }

    public Map<String, byte[]> extractZipFiles(byte[] zipBytes) {
        try {
            Map<String, byte[]> files = new HashMap<>();
            ZipFileExtractor.consumeZip(zipBytes, (entry, is) -> {
                try(InputStream fis = is) {
                    files.put(entry.getName(), fis.readAllBytes());
                } catch (IOException e) {
                    log.error(e.getMessage());
                    throw new BusinessException(ErrorCode.INTERNAL_SERVER);
                }
            });
            return files;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER);
        }
    }
}
