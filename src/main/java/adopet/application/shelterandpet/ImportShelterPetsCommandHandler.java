package adopet.application.shelterandpet;

import adopet.exception.InvalidUserInputException;
import adopet.gateway.PetGateway;

public class ImportShelterPetsCommandHandler {

    private final PetGateway petGateway;

    public ImportShelterPetsCommandHandler(PetGateway petGateway) {
        this.petGateway = petGateway;
    }

    public int execute(String shelterIdOrName, String csvFileName) {
        if (shelterIdOrName == null || shelterIdOrName.isBlank()) {
            throw new InvalidUserInputException("Id ou nome do abrigo não pode ser vazio.");
        }
        if (csvFileName == null || csvFileName.isBlank()) {
            throw new InvalidUserInputException("Nome do arquivo CSV não pode ser vazio.");
        }
        return petGateway.importPets(shelterIdOrName.trim(), csvFileName.trim());
    }
}