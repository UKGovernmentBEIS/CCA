package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service;

import java.util.List;

public class PerformanceAccountTemplateProcessingException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private List<String> errors;
	
	public PerformanceAccountTemplateProcessingException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }
	
	public PerformanceAccountTemplateProcessingException(List<String> errors) {
        this.errors = errors;
    }
	
    public List<String> getErrors() {
        return errors;
    }

    public void addError(String error) {
        this.errors.add(error);
    }
    
    @Override
    public String getMessage() {
        return super.getMessage() + " Errors: " + errors.toString();
    }

}
