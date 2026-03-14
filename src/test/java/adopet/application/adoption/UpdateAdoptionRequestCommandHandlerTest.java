package adopet.application.adoption;

import adopet.domain.adoption.*;
import adopet.exception.EntityNotFoundException;
import adopet.exception.InvalidUserInputException;
import adopet.infrastructure.memory.InMemoryAdoptionRequestGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateAdoptionRequestCommandHandlerTest {

    private InMemoryAdoptionRequestGateway gateway;
    private UpdateAdoptionRequestCommandHandler handler;
    private AdoptionRequest created;

    @BeforeEach
    void setUp() {
        gateway = new InMemoryAdoptionRequestGateway();
        handler = new UpdateAdoptionRequestCommandHandler(gateway);

        created = gateway.registerAdoptionRequest(
                AdoptionRequest.newRequest(
                        1L,
                        10L,
                        new ApplicantName("Maria"),
                        new ApplicantEmail("maria@email.com"),
                        new ApplicantPhone("31999999999"),
                        new ApplicantDocument("12345678900"),
                        HousingType.HOUSE,
                        true,
                        new ReasonText("Quero adotar com responsabilidade e carinho."),
                        EligibilityAnalysis.REQUIRES_MANUAL_REVIEW
                )
        );
    }

    @Test
    void shouldUpdateRequest() {
        AdoptionRequest updated = handler.execute(
                created.id(),
                2L,
                20L,
                "Maria Editada",
                "editada@email.com",
                "31888888888",
                "11122233344",
                "APARTMENT",
                false,
                "Agora tenho uma justificativa melhor e mais completa."
        );

        assertEquals(2L, updated.petId());
        assertEquals(20L, updated.shelterId());
        assertEquals("Maria Editada", updated.applicantName().value());
        assertEquals("editada@email.com", updated.applicantEmail().value());
        assertEquals("31888888888", updated.applicantPhone().value());
        assertEquals("11122233344", updated.applicantDocument().value());
        assertEquals(HousingType.APARTMENT, updated.housingType());
        assertFalse(updated.hasOtherPets());
    }

    @Test
    void shouldFailWhenIdIsNull() {
        assertThrows(InvalidUserInputException.class, () ->
                handler.execute(
                        null,
                        2L,
                        20L,
                        "Maria Editada",
                        "editada@email.com",
                        "31888888888",
                        "11122233344",
                        "APARTMENT",
                        false,
                        "Agora tenho uma justificativa melhor e mais completa."
                )
        );
    }

    @Test
    void shouldFailWhenRequestDoesNotExist() {
        assertThrows(EntityNotFoundException.class, () ->
                handler.execute(
                        999L,
                        2L,
                        20L,
                        "Maria Editada",
                        "editada@email.com",
                        "31888888888",
                        "11122233344",
                        "APARTMENT",
                        false,
                        "Agora tenho uma justificativa melhor e mais completa."
                )
        );
    }
}