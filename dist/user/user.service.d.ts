import { Model } from 'mongoose';
import { User } from '../interfaces/user.interface';
export declare class UserService {
    private userModel;
    constructor(userModel: Model<User>);
    createUser(email: string, password: string): Promise<{
        success: boolean;
        message: string;
    }>;
    validateUser(email: string, password: string): Promise<{
        success: boolean;
        user?: any;
        message?: string;
    }>;
}
