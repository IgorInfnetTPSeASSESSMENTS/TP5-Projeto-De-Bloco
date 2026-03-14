package adopet.application.adoption;

import adopet.domain.adoption.AdoptionRequest;
import adopet.domain.adoption.AdoptionRequestStatus;
import adopet.domain.adoption.ApplicantDocument;
import adopet.domain.adoption.ApplicantEmail;
import adopet.domain.adoption.ApplicantName;
import adopet.domain.adoption.ApplicantPhone;
import adopet.domain.adoption.EligibilityAnalysis;
import adopet.domain.adoption.HousingType;
import adopet.domain.adoption.ReasonText;
import adopet.exception.InvalidUserInputException;
import adopet.gateway.AdoptionRequestGateway;
import adopet.gateway.PetGateway;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DeleteAdoptionRequestCommandHandlerTest {

    @Test
    void shouldDeleteRequest() {
        AdoptionRequestGateway gateway = mock(AdoptionRequestGateway.class);
        PetGateway petGateway = mock(PetGateway.class);

        AdoptionRequest existing = new AdoptionRequest(
                1L,
                10L,
                20L,
                new ApplicantName("Maria"),
                new ApplicantEmail("maria@email.com"),
                new ApplicantPhone("31999999999"),
                new ApplicantDocument("12345678900"),
                HousingType.HOUSE,
                true,
                new ReasonText("Quero adotar com responsabilidade e carinho."),
                AdoptionRequestStatus.PENDING,
                EligibilityAnalysis.REQUIRES_MANUAL_REVIEW,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(gateway.findById(1L)).thenReturn(Optional.of(existing));

        DeleteAdoptionRequestCommandHandler handler =
                new DeleteAdoptionRequestCommandHandler(gateway, petGateway);

        handler.execute(1L);

        verify(gateway).findById(1L);
        verify(gateway).deleteAdoptionRequest(1L);
        verifyNoInteractions(petGateway);
    }

    @Test
    void shouldFailWhenIdIsNull() {
        AdoptionRequestGateway gateway = mock(AdoptionRequestGateway.class);
        PetGateway petGateway = mock(PetGateway.class);

        DeleteAdoptionRequestCommandHandler handler =
                new DeleteAdoptionRequestCommandHandler(gateway, petGateway);

        assertThrows(
                InvalidUserInputException.class,
                () -> handler.execute(null)
        );
    }
}