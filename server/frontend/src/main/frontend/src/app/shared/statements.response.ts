export class StatementsResponse {
    timeLimit: number;
    memoryLimit: number;

    inputFile:string;
    outputFile: string;

    name: string;
    legend: string;
    input: string;
    output: string;
    sampleTests: SampleTest[];
    notes: string;

    hasPdf: boolean;

    error: string;
}

export class SampleTest {
    input: string;
    output: string;
}
