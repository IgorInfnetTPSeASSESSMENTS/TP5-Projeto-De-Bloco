package adopet.web.dto.shelterandpet;

import jakarta.validation.constraints.NotBlank;

public class PetExportForm {

    @NotBlank(message = "O caminho do arquivo CSV é obrigatório.")
    private String csvFileName;

    public PetExportForm() {
    }

    public PetExportForm(String csvFileName) {
        this.csvFileName = csvFileName;
    }

    public String getCsvFileName() {
        return csvFileName;
    }

    public void setCsvFileName(String csvFileName) {
        this.csvFileName = csvFileName;
    }
}