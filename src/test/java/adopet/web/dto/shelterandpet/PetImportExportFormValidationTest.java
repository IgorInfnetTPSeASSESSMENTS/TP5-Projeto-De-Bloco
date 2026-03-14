package adopet.web.dto.shelterandpet;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PetImportExportFormValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateBlankImportCsvFileName() {
        PetImportForm form = new PetImportForm("");

        Set<ConstraintViolation<PetImportForm>> violations = validator.validate(form);

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("O caminho do arquivo CSV é obrigatório.")));
    }

    @Test
    void shouldValidateBlankExportCsvFileName() {
        PetExportForm form = new PetExportForm("");

        Set<ConstraintViolation<PetExportForm>> violations = validator.validate(form);

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("O caminho do arquivo CSV é obrigatório.")));
    }
}