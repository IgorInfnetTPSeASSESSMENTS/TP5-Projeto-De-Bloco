package adopet.domain.shelterandpet;

import adopet.exception.InvalidUserInputException;

/*
 * PetName: pequeno value object que encapsula validação e normalização do nome do pet.
 * - Rejeita valores nulos/vazios e nomes excessivamente longos.
 * - Remove espaços em branco e expõe o valor limpo via `value()`.
 */
public final class PetName {

    private final String value;

    public PetName(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidUserInputException("Nome do pet não pode ser vazio.");
        }

        if (value.length() > 60) {
            throw new InvalidUserInputException("Nome do pet muito longo.");
        }

        this.value = value.trim();
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}