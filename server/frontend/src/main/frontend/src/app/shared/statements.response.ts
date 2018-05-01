import {Test} from "./test";

export class StatementsResponse {
    timeLimit: number;
    memoryLimit: number;

    inputFile:string;
    outputFile: string;

    name: string;
    legend: string;
    input: string;
    output: string;
    sampleTests: Test[];
    notes: string;

    hasPdf: boolean;

    error: string;
}
