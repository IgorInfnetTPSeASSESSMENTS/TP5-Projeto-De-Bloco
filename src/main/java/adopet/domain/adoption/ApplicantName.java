package adopet.domain.adoption;

import adopet.exception.InvalidUserInputException;

public final class ApplicantName {

    private final String value;

    public ApplicantName(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidUserInputException("Nome do solicitante não pode ser vazio.");
        }

        String normalized = value.trim();
        if (normalized.length() < 3) {
            throw new InvalidUserInputException("Nome do solicitante muito curto.");
        }
        if (normalized.length() > 100) {
            throw new InvalidUserInputException("Nome do solicitante muito longo.");
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
}
