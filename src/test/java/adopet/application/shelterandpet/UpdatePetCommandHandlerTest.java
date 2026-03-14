package adopet.application.shelterandpet;


import adopet.domain.shelterandpet.*;
import adopet.exception.InvalidUserInputException;
import adopet.fakes.AbstractFakePetGateway;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UpdatePetCommandHandlerTest {

    @Test
    void shouldRejectNullPetId() {
        UpdatePetCommandHandler handler = new UpdatePetCommandHandler(new NoopPetGateway());

        InvalidUserInputException ex = assertThrows(InvalidUserInputException.class,
                () -> handler.execute(null, "GATO", "Mimi", "SRD", 2, "Branco", 3.2));

        assertEquals("Id do pet não pode ser vazio.", ex.getMessage());
    }

    @Test
    void shouldRejectBlankType() {
        UpdatePetCommandHandler handler = new UpdatePetCommandHandler(new NoopPetGateway());

        InvalidUserInputException ex1 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute(1L, null, "Mimi", "SRD", 2, "Branco", 3.2));
        assertEquals("Tipo não pode ser vazio.", ex1.getMessage());

        InvalidUserInputException ex2 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute(1L, "   ", "Mimi", "SRD", 2, "Branco", 3.2));
        assertEquals("Tipo não pode ser vazio.", ex2.getMessage());
    }

    @Test
    void shouldRejectBlankName() {
        UpdatePetCommandHandler handler = new UpdatePetCommandHandler(new NoopPetGateway());

        InvalidUserInputException ex1 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute(1L, "GATO", null, "SRD", 2, "Branco", 3.2));
        assertEquals("Nome do pet não pode ser vazio.", ex1.getMessage());

        InvalidUserInputException ex2 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute(1L, "GATO", "   ", "SRD", 2, "Branco", 3.2));
        assertEquals("Nome do pet não pode ser vazio.", ex2.getMessage());
    }

    @Test
    void shouldRejectBlankBreed() {
        UpdatePetCommandHandler handler = new UpdatePetCommandHandler(new NoopPetGateway());

        InvalidUserInputException ex1 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute(1L, "GATO", "Mimi", null, 2, "Branco", 3.2));
        assertEquals("Raça não pode ser vazia.", ex1.getMessage());

        InvalidUserInputException ex2 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute(1L, "GATO", "Mimi", "   ", 2, "Branco", 3.2));
        assertEquals("Raça não pode ser vazia.", ex2.getMessage());
    }

    @Test
    void shouldRejectBlankColor() {
        UpdatePetCommandHandler handler = new UpdatePetCommandHandler(new NoopPetGateway());

        InvalidUserInputException ex1 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute(1L, "GATO", "Mimi", "SRD", 2, null, 3.2));
        assertEquals("Cor não pode ser vazia.", ex1.getMessage());

        InvalidUserInputException ex2 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute(1L, "GATO", "Mimi", "SRD", 2, "   ", 3.2));
        assertEquals("Cor não pode ser vazia.", ex2.getMessage());
    }

    @Test
    void shouldTrimInputsAndCallGatewayUpdate() {
        class SpyGateway extends NoopPetGateway {
            Long lastId;
            Pet lastPet;

            @Override
            public Optional<Pet> findById(Long petId) {
                return Optional.of(new Pet(
                        petId,
                        PetType.CACHORRO,
                        new PetName("Original"),
                        "Original Breed",
                        new AgeYears(1),
                        "Original Color",
                        new WeightKg(2.5),
                        PetStatus.AVAILABLE
                ));
            }

            @Override
            public Pet updatePet(Long petId, Pet updated) {
                this.lastId = petId;
                this.lastPet = updated;

                return new Pet(
                        updated.id(),
                        updated.type(),
                        updated.name(),
                        updated.breed(),
                        updated.age(),
                        updated.color(),
                        updated.weight(),
                        updated.status()
                );
            }
        }

        SpyGateway gateway = new SpyGateway();
        UpdatePetCommandHandler handler = new UpdatePetCommandHandler(gateway);

        Pet res = handler.execute(10L, "  GATO  ", "  Mimi ", " SRD ", 2, " Branco ", 3.2);

        assertEquals(10L, res.id());
        assertEquals(10L, gateway.lastId);

        assertNotNull(gateway.lastPet);
        assertEquals(10L, gateway.lastPet.id());
        assertEquals(PetType.GATO, gateway.lastPet.type());
        assertEquals("Mimi", gateway.lastPet.name().value());
        assertEquals("SRD", gateway.lastPet.breed());
        assertEquals(2, gateway.lastPet.age().value());
        assertEquals("Branco", gateway.lastPet.color());
        assertEquals(3.2, gateway.lastPet.weight().value(), 0.0001);
        assertEquals(PetStatus.AVAILABLE, gateway.lastPet.status());
    }

    private static class NoopPetGateway extends AbstractFakePetGateway {
    }
}