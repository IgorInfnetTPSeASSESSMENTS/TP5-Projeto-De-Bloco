package adopet.application.shelterandpet;

import adopet.exception.InvalidUserInputException;
import adopet.fakes.AbstractFakePetGateway;
import adopet.gateway.PetGateway;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExportShelterPetsCommandHandlerTest {

    @Test
    void shouldRejectBlankShelterIdOrName() {
        PetGateway gw = new AbstractFakePetGateway() { };

        ExportShelterPetsCommandHandler handler = new ExportShelterPetsCommandHandler(gw);

        assertThrows(InvalidUserInputException.class, () -> handler.execute(null, "out.csv"));
        assertThrows(InvalidUserInputException.class, () -> handler.execute("   ", "out.csv"));
    }

    @Test
    void shouldRejectBlankCsvFileName() {
        PetGateway gw = new AbstractFakePetGateway() { };

        ExportShelterPetsCommandHandler handler = new ExportShelterPetsCommandHandler(gw);

        assertThrows(InvalidUserInputException.class, () -> handler.execute("1", null));
        assertThrows(InvalidUserInputException.class, () -> handler.execute("1", "  "));
    }

    @Test
    void shouldTrimInputsAndReturnExportedCount() {
        PetGateway gw = new AbstractFakePetGateway() {
            @Override
            public int exportPets(String shelterIdOrName, String csvFileName) {
                assertEquals("1", shelterIdOrName);
                assertEquals("/tmp/out.csv", csvFileName);
                return 2;
            }
        };

        ExportShelterPetsCommandHandler handler = new ExportShelterPetsCommandHandler(gw);

        int exported = handler.execute(" 1 ", " /tmp/out.csv ");
        assertEquals(2, exported);
    }
}