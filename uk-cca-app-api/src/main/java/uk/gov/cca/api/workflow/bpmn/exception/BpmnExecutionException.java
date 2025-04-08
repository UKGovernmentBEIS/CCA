package uk.gov.cca.api.workflow.bpmn.exception;

import lombok.Getter;

import java.io.Serial;
import java.util.List;

@Getter
public class BpmnExecutionException extends Exception {

	@Serial
    private static final long serialVersionUID = 1L;
	
	private List<String> errors;
	
	public BpmnExecutionException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }
	
	public BpmnExecutionException(List<String> errors) {
        this.errors = errors;
    }

    public void addError(String error) {
        this.errors.add(error);
    }
    
    @Override
    public String getMessage() {
        return super.getMessage() + " Errors: " + errors.toString();
    }
}
