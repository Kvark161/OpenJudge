export class User {
    id: number;
    username: string;
    password: string;
    role: string;
    isBlocked: boolean;

    passwordVisible = false;

    public isAdmin(): boolean {
        return this.role == "ADMIN";
    }

    static copyOf(other: User): User {
        let user = new User();
        user.id = other.id;
        user.username = other.username;
        user.password = other.password;
        user.role = other.role;
        user.passwordVisible = other.passwordVisible;
        user.isBlocked = other.isBlocked;
        return user;
    }
}
