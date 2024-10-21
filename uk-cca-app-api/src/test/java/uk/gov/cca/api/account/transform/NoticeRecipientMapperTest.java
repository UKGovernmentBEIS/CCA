package uk.gov.cca.api.account.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.cca.api.account.domain.dto.AdditionalNoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;

import static org.assertj.core.api.Assertions.assertThat;

public class NoticeRecipientMapperTest {

    private final NoticeRecipientMapper mapper = Mappers.getMapper(NoticeRecipientMapper.class);

    @Test
    void toOperatorNoticeRecipientDTO() {
        final UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .userId("userId")
                .firstName("fn")
                .lastName("ln")
                .email("email")
                .build();

        final AdditionalNoticeRecipientDTO noticeRecipientDTO = AdditionalNoticeRecipientDTO.builder()
        		.userId("userId")
                .firstName("fn")
                .lastName("ln")
                .email("email")
                .type(NoticeRecipientType.OPERATOR)
                .build();

        AdditionalNoticeRecipientDTO operatorNoticeRecipientDTO = mapper.toOperatorNoticeRecipientDTO(userInfoDTO);
        assertThat(operatorNoticeRecipientDTO).isEqualTo(noticeRecipientDTO);
    }

    @Test
    void toSectorUSerNoticeRecipientDTO() {
        final UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .userId("userId")
                .firstName("fn")
                .lastName("ln")
                .email("email")
                .build();

        final AdditionalNoticeRecipientDTO noticeRecipientDTO = AdditionalNoticeRecipientDTO.builder()
        		.userId("userId")
                .firstName("fn")
                .lastName("ln")
                .email("email")
                .type(NoticeRecipientType.SECTOR_USER)
                .build();

        AdditionalNoticeRecipientDTO operatorNoticeRecipientDTO = mapper.toSectorUSerNoticeRecipientDTO(userInfoDTO);
        assertThat(operatorNoticeRecipientDTO).isEqualTo(noticeRecipientDTO);
    }
}
