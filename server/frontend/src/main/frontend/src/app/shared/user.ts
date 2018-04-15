export class User {
    id: number;
    username: string;
    password: string;
    passwordVisible = false;
    isAdmin: boolean = false;

    static fromServer(obj) {
        let user = new User();
        user.id = obj.id;
        user.username = obj.username;
        user.password = obj.password;
        user.isAdmin = obj.role == 'ADMIN';
        return user;
    }
}
