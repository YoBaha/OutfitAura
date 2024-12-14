
import { Controller, Post, Body } from '@nestjs/common';
import { UserService } from './user.service';
import { SignUpRequest } from '../dto/signup.dto';

@Controller('user')
export class UserController {
    constructor(private readonly userService: UserService) {}

    @Post('signup')
    async signup(@Body() body: SignUpRequest) {
        return this.userService.createUser(body.email, body.password);
    }

    @Post('login')
    async login(@Body() body: { email: string; password: string }) {
        return this.userService.validateUser(body.email, body.password);
    }

    @Post('google-signin')
    async googleSignIn(@Body() body: { idToken: string }) {
        return this.userService.handleGoogleSignIn(body.idToken);
    }
}

