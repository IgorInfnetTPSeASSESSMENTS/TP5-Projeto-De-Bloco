package adopet.domain.shelterandpet;

import adopet.exception.InvalidUserInputException;

import java.util.Objects;

public final class PhoneNumber {

    private final String value;

    public PhoneNumber(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidUserInputException("Telefone não pode ser vazio.");
        }

        if (value.length() < 8) {
            throw new InvalidUserInputException("Telefone inválido: " + value);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhoneNumber that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}