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
import adopet.exception.EntityNotFoundException;
import adopet.exception.InvalidUserInputException;
import adopet.gateway.PetGateway;
import adopet.infrastructure.memory.InMemoryAdoptionRequestGateway;
import adopet.infrastructure.memory.ProgrammableEligibilityAnalysisGateway;
import adopet.infrastructure.memory.ScenarioMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RetryEligibilityAnalysisCommandHandlerTest {

    private InMemoryAdoptionRequestGateway gateway;
    private ProgrammableEligibilityAnalysisGateway eligibilityGateway;
    private PetGateway petGateway;
    private RetryEligibilityAnalysisCommandHandler handler;
    private AdoptionRequest created;

    @BeforeEach
    void setUp() {
        gateway = new InMemoryAdoptionRequestGateway();
        eligibilityGateway = new ProgrammableEligibilityAnalysisGateway();
        petGateway = mock(PetGateway.class);

        when(petGateway.findById(1L)).thenReturn(Optional.of(mock(adopet.domain.shelterandpet.Pet.class)));

        handler = new RetryEligibilityAnalysisCommandHandler(
                gateway,
                eligibilityGateway,
                petGateway
        );

        created = gateway.registerAdoptionRequest(
                AdoptionRequest.newRequest(
                        1L,
                        10L,
                        new ApplicantName("Maria da Silva"),
                        new ApplicantEmail("maria@email.com"),
                        new ApplicantPhone("31999999999"),
                        new ApplicantDocument("12345678900"),
                        HousingType.HOUSE,
                        true,
                        new ReasonText("Quero adotar com responsabilidade e carinho."),
                        EligibilityAnalysis.UNAVAILABLE
                )
        );
    }

    @Test
    void shouldRetryAnalysisSuccessfully() {
        eligibilityGateway.setScenarioMode(ScenarioMode.SUCCESS);
        eligibilityGateway.setSuccessResult(EligibilityAnalysis.ELIGIBLE);

        AdoptionRequest result = handler.execute(created.id());

        assertEquals(EligibilityAnalysis.ELIGIBLE, result.eligibilityAnalysis());
        assertEquals(AdoptionRequestStatus.UNDER_REVIEW, result.status());
    }

    @Test
    void shouldFallbackToUnavailableWhenTimeoutOccurs() {
        eligibilityGateway.setScenarioMode(ScenarioMode.TIMEOUT);

        AdoptionRequest result = handler.execute(created.id());

        assertEquals(EligibilityAnalysis.UNAVAILABLE, result.eligibilityAnalysis());
        assertEquals(AdoptionRequestStatus.PENDING, result.status());
    }

    @Test
    void shouldFallbackToUnavailableWhenNetworkErrorOccurs() {
        eligibilityGateway.setScenarioMode(ScenarioMode.NETWORK_ERROR);

        AdoptionRequest result = handler.execute(created.id());

        assertEquals(EligibilityAnalysis.UNAVAILABLE, result.eligibilityAnalysis());
        assertEquals(AdoptionRequestStatus.PENDING, result.status());
    }

    @Test
    void shouldFailWhenIdIsNull() {
        assertThrows(InvalidUserInputException.class, () -> handler.execute(null));
    }

    @Test
    void shouldFailWhenRequestDoesNotExist() {
        assertThrows(EntityNotFoundException.class, () -> handler.execute(999L));
    }
}