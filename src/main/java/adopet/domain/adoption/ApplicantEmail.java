package adopet.domain.adoption;

import adopet.exception.InvalidUserInputException;

import java.util.Objects;

public final class ApplicantEmail {

    private final String value;

    public ApplicantEmail(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidUserInputException("Email do solicitante não pode ser vazio.");
        }

        String normalized = value.trim().toLowerCase();
        if (!normalized.contains("@") || normalized.startsWith("@") || normalized.endsWith("@")) {
            throw new InvalidUserInputException("Email do solicitante inválido: " + value);
        }
        if (normalized.length() > 120) {
            throw new InvalidUserInputException("Email do solicitante muito longo.");
        }

        this.value = normalized;
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
        if (!(o instanceof ApplicantEmail that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}