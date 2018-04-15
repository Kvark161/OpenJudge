export class ValidationResult {
    public static generalErrorPath = "generalError";

    errors;//path -> list of errors descriptions

    static getEmpty(): ValidationResult {
        let result = new ValidationResult();
        result.errors = {};
        return result;
    }

    setErrors(errors) {
        this.errors = errors;
    }

    isEmpty() {
        for(var key in this.errors) {
            if (this.errors.hasOwnProperty(key)) {
                return false;
            }
        }
        return true;
    }

    setGeneralError(description: string) {
        this.errors[ValidationResult.generalErrorPath] = [description];
    }

    addError(path: string, description: string) {
        if (!this.errors[path]) {
            this.errors[path] = [];
        }
        this.errors[path].push(description);
    }

}
