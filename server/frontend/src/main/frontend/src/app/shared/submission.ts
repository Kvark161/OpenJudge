import {User} from "./user";

export class Submission {
    id: number;
    user: User = new User();
    username: string;
    contestId: number;
    problemId: number;
    status: string;
    usedTime: number;
    usedMemory: number;
    passedTests: number;
    numberTests: number;
    sourceCode: string;
}
