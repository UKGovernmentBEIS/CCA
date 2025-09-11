package uk.gov.cca.api.workflow.request.flow.common.configuration;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class WorkbookFactoryConfiguration {
    
    @Bean
    public CustomWorkbookFactory workbookFactoryBean() {
        WorkbookFactory.addProvider(new XSSFWorkbookFactory());
        return new CustomWorkbookFactory();
    }
    
    public static class CustomWorkbookFactory {
        public Workbook create(InputStream in) throws IOException {
            return WorkbookFactory.create(in);
        }
    }
}

