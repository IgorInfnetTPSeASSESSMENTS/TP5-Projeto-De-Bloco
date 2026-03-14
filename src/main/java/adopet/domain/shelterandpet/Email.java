package adopet.domain.shelterandpet;

import adopet.exception.InvalidUserInputException;

import java.util.Objects;

public final class Email {

    private final String value;

    public Email(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidUserInputException("Email não pode ser vazio.");
        }

        if (!value.contains("@")) {
            throw new InvalidUserInputException("Email inválido: " + value);
        }

        this.value = value.trim().toLowerCase();
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
        if (!(o instanceof Email email)) return false;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}