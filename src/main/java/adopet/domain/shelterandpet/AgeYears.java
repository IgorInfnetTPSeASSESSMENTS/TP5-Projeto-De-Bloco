package adopet.domain.shelterandpet;

import adopet.exception.InvalidUserInputException;

public final class AgeYears {

    private final int value;

    public AgeYears(int value) {
        if (value < 0) {
            throw new InvalidUserInputException("Idade não pode ser negativa.");
        }

        if (value > 40) {
            throw new InvalidUserInputException("Idade inválida para pet: " + value);
        }

        this.value = value;
    }

    public int value() {
        return value;
    }

}