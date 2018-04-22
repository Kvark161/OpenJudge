package eskimo.backend.rest.response;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Common response for add or edit entity request
 */
@Getter
public class ValidationResult {
    private Map<String, List<String>> errors = new HashMap<>();

    public void addError(String path, String error) {
        errors.computeIfAbsent(path, key -> new ArrayList<>()).add(error);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean hasErrorsOnField(String path) {
        return errors.containsKey(path);
    }
}
