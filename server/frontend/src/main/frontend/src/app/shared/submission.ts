import {Contest} from "./contest";
import {Problem} from "./problem";

export class Submission {
    id: number;
    username: string;
    contest: Contest;
    problem: Problem;
    verdict: string;

}
