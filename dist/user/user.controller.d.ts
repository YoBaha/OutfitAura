import { UserService } from './user.service';
import { SignUpRequest } from '../dto/signup.dto';
export declare class UserController {
    private readonly userService;
    constructor(userService: UserService);
    signup(body: SignUpRequest): Promise<{
        success: boolean;
        message: string;
    }>;
    login(body: {
        email: string;
        password: string;
    }): Promise<{
        success: boolean;
        user?: any;
        message?: string;
    }>;
}
