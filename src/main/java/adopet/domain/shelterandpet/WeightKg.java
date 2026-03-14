package adopet.domain.shelterandpet;

import adopet.exception.InvalidUserInputException;

public final class WeightKg {

    private final double value;

    public WeightKg(double value) {
        if (value <= 0) {
            throw new InvalidUserInputException("Peso deve ser positivo.");
        }

        if (value > 150) {
            throw new InvalidUserInputException("Peso inválido: " + value);
        }

        this.value = value;
    }

    public double value() {
        return value;
    }
}
