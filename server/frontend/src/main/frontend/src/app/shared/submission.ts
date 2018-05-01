export class Submission {
    id: number;
    userId: number;
    username: string;
    contestId: number;
    problemId: number;
    problemIndex: number;
    status: string;
    usedTime: number;
    usedMemory: number;
    passedTests: number;
    firstFailTest: number;
    numberTests: number;
    sourceCode: string;
    programmingLanguageId: number;
    message: string;
}
