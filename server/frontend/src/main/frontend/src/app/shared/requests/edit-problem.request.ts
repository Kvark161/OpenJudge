import {Test} from "../test";

export class EditProblemRequest {
    timeLimit: number;
    memoryLimit: number;
    checkerFile: File;

    name: string;
    legend: string;
    input: string;
    output: string;
    notes: string;
    statementsPdf: File;

    tests: Test[];

    checkerExists: boolean;
    statementsPdfExists: boolean;
}
