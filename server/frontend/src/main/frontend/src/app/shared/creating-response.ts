import {ValidationResult} from "./validation-response";

export class CreatingResponse{
    validationResult: ValidationResult;
    changedObject;

    /**
     * @param json - input json object
     * @param objectMapper - function that takes json object and return necessary js object
     * @returns {CreatingResponse}
     */
    static fromJson(json, objectMapper): CreatingResponse {
        let result: CreatingResponse = new CreatingResponse();
        result.validationResult = json.validationResult;
        if (json.changedObject != null) {
            result.changedObject = objectMapper(json.changedObject);
        }
        return result;
    }
}
