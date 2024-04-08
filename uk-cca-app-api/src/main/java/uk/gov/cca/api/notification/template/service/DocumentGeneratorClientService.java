package uk.gov.cca.api.notification.template.service;

public interface DocumentGeneratorClientService {

    byte[] generateDocument(byte[] source, String fileNameToGenerate) throws Exception;

}
