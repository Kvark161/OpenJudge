export class Contest {
    id: number;
    name: string;
    startTime: string;
    duration: number;

    constructor(id: number, name: string, startTime: string, duration: number) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.duration = duration;
    }

}