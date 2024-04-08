package uk.gov.cca.api.workflow.payment.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.cca.api.workflow.payment.repository.PaymentFeeMethodRepository;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.cca.api.workflow.payment.domain.PaymentFeeMethod;
import uk.gov.cca.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class PaymentFeeMethodRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private PaymentFeeMethodRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByCompetentAuthorityAndRequestType() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        RequestType requestType = RequestType.DUMMY_REQUEST_TYPE;
        PaymentFeeMethod paymentFeeMethod = PaymentFeeMethod.builder()
            .competentAuthority(competentAuthority)
            .requestType(requestType)
            .type(FeeMethodType.STANDARD)
            .build();

        entityManager.persist(paymentFeeMethod);

        flushAndClear();

        Optional<PaymentFeeMethod> optionalResult =
            repository.findByCompetentAuthorityAndRequestType(competentAuthority, requestType);

        assertThat(optionalResult).isNotEmpty();
        assertEquals(paymentFeeMethod, optionalResult.get());
    }

    @Test
    void findByCompetentAuthorityAndRequestType_no_result() {
        RequestType requestType = RequestType.DUMMY_REQUEST_TYPE;
        PaymentFeeMethod paymentFeeMethod = PaymentFeeMethod.builder()
            .competentAuthority(CompetentAuthorityEnum.SCOTLAND)
            .requestType(requestType)
            .type(FeeMethodType.STANDARD)
            .build();

        entityManager.persist(paymentFeeMethod);

        flushAndClear();

        Optional<PaymentFeeMethod> optionalResult =
            repository.findByCompetentAuthorityAndRequestType(CompetentAuthorityEnum.WALES, requestType);

        assertThat(optionalResult).isEmpty();
    }

    @Test
    void findByCompetentAuthorityAndRequestTypeAndType() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        RequestType requestType = RequestType.DUMMY_REQUEST_TYPE;
        FeeMethodType feeMethodType = FeeMethodType.STANDARD;
        PaymentFeeMethod paymentFeeMethod = PaymentFeeMethod.builder()
            .competentAuthority(competentAuthority)
            .requestType(requestType)
            .type(feeMethodType)
            .build();

        entityManager.persist(paymentFeeMethod);

        flushAndClear();

        Optional<PaymentFeeMethod> optionalResult =
            repository.findByCompetentAuthorityAndRequestTypeAndType(competentAuthority, requestType, feeMethodType);

        assertThat(optionalResult).isNotEmpty();
        assertEquals(paymentFeeMethod, optionalResult.get());
    }

    @Test
    void findByCompetentAuthorityAndRequestTypeAndType_no_result() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        RequestType requestType = RequestType.DUMMY_REQUEST_TYPE;
        PaymentFeeMethod paymentFeeMethod = PaymentFeeMethod.builder()
            .competentAuthority(competentAuthority)
            .requestType(requestType)
            .type(FeeMethodType.STANDARD)
            .build();

        entityManager.persist(paymentFeeMethod);

        flushAndClear();

        Optional<PaymentFeeMethod> optionalResult =
            repository.findByCompetentAuthorityAndRequestTypeAndType(competentAuthority, requestType, FeeMethodType.INSTALLATION_CATEGORY_BASED);

        assertThat(optionalResult).isEmpty();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}