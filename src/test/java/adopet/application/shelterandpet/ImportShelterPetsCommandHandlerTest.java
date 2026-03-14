package adopet.application.shelterandpet;

import adopet.exception.InvalidUserInputException;
import adopet.fakes.AbstractFakePetGateway;
import adopet.gateway.PetGateway;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImportShelterPetsCommandHandlerTest {

    @Test
    void shouldRejectBlankShelterIdOrName() {
        PetGateway gateway = new AbstractFakePetGateway() {
            @Override
            public int importPets(String shelterIdOrName, String csvFileName) {
                fail("não deve chamar gateway se abrigo inválido");
                return 0;
            }
        };

        ImportShelterPetsCommandHandler handler = new ImportShelterPetsCommandHandler(gateway);

        InvalidUserInputException ex1 =
                assertThrows(InvalidUserInputException.class, () -> handler.execute(null, "pets.csv"));
        assertEquals("Id ou nome do abrigo não pode ser vazio.", ex1.getMessage());

        InvalidUserInputException ex2 =
                assertThrows(InvalidUserInputException.class, () -> handler.execute("   ", "pets.csv"));
        assertEquals("Id ou nome do abrigo não pode ser vazio.", ex2.getMessage());
    }

    @Test
    void shouldRejectBlankCsvFileName() {
        PetGateway gateway = new AbstractFakePetGateway() {
            @Override
            public int importPets(String shelterIdOrName, String csvFileName) {
                fail("não deve chamar gateway se csv inválido");
                return 0;
            }
        };

        ImportShelterPetsCommandHandler handler = new ImportShelterPetsCommandHandler(gateway);

        InvalidUserInputException ex1 =
                assertThrows(InvalidUserInputException.class, () -> handler.execute("1", null));
        assertEquals("Nome do arquivo CSV não pode ser vazio.", ex1.getMessage());

        InvalidUserInputException ex2 =
                assertThrows(InvalidUserInputException.class, () -> handler.execute("1", "   "));
        assertEquals("Nome do arquivo CSV não pode ser vazio.", ex2.getMessage());
    }

    @Test
    void shouldTrimAndReturnGatewayCount() {
        class SpyGateway extends AbstractFakePetGateway {
            String lastShelter;
            String lastCsv;

            @Override
            public int importPets(String shelterIdOrName, String csvFileName) {
                this.lastShelter = shelterIdOrName;
                this.lastCsv = csvFileName;
                return 3;
            }
        }

        SpyGateway gateway = new SpyGateway();
        ImportShelterPetsCommandHandler handler = new ImportShelterPetsCommandHandler(gateway);

        int count = handler.execute("  shelter1  ", "  pets.csv  ");

        assertEquals(3, count);
        assertEquals("shelter1", gateway.lastShelter);
        assertEquals("pets.csv", gateway.lastCsv);
    }
}