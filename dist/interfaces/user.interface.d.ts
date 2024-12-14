export interface User {
    email: string;
    password: string;
    resetToken?: string;
    resetTokenDate?: Date;
}
