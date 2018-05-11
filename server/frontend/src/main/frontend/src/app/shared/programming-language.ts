export class ProgrammingLanguage {
    id: number;
    name: string;
    description: string;
    compilerPath: string;
    isCompiled: boolean = false;
    interpreterPath: string;
    extension: string;
    binaryExtension: string;
    compileCommand: string;
    runCommand: string;
    compilationMemoryLimit: number = 5242880;
    compilationTimeLimit: number = 30000;

    static fromServer(serverPL): ProgrammingLanguage {
        let res: ProgrammingLanguage = new ProgrammingLanguage();
        res.id = serverPL.id;
        res.name = serverPL.name;
        res.description = serverPL.description;
        res.compilerPath = serverPL.compilerPath;
        res.isCompiled = serverPL.isCompiled;
        res.interpreterPath = serverPL.interpreterPath;
        res.extension = serverPL.extension;
        res.binaryExtension = serverPL.binaryExtension;
        let ccServer: string[] = serverPL.compileCommand;
        res.compileCommand = ccServer.join(" ");
        let rcServer: string[] = serverPL.runCommand;
        res.runCommand = rcServer.join(" ");
        res.compilationMemoryLimit = serverPL.compilationMemoryLimit;
        res.compilationTimeLimit = serverPL.compilationTimeLimit;
        return res;
    }

    private copy(): ProgrammingLanguage {
        let res = new ProgrammingLanguage();
        res.id = this.id;
        res.name = this.name;
        res.description = this.description;
        res.compilerPath = this.compilerPath;
        res.isCompiled = this.isCompiled;
        res.interpreterPath = this.interpreterPath;
        res.extension = this.extension;
        res.binaryExtension = this.binaryExtension;
        res.compileCommand = this.compileCommand;
        res.runCommand = this.runCommand;
        res.compilationMemoryLimit = this.compilationMemoryLimit;
        res.compilationTimeLimit = this.compilationTimeLimit;
        return res;
    }

    public toServer() {
        let res: any = this.copy();
        if (res.compileCommand) {
            res.compileCommand = res.compileCommand.split(" ");
        }
        res.runCommand = res.runCommand.split(" ");
        return res;
    }

}
