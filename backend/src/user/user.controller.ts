import { Controller, Post, Body } from '@nestjs/common';
import { UserService } from './user.service';
import { SignUpRequest, SignUpResponse } from './sign-up.dto';

@Controller('user')
export class UserController {
  constructor(private userService: UserService) {}

  @Post('login')
  async login(@Body() body: { email: string; password: string }) {
    const user = await this.userService.validateUser(body.email, body.password);

    if (user) {
      return { success: true, user };
    } else {
      return { success: false, message: 'Invalid credentials' };
    }
  }

  @Post('register')
  async register(@Body() body: SignUpRequest): Promise<SignUpResponse> {
    const existingUser = await this.userService.findUserByEmail(body.email);
    if (existingUser) {
      return { success: false, message: 'Email already registered' };
    }

    const newUser = await this.userService.createUser(body.email, body.password);
    return {
      success: true,
      message: 'User successfully created',
      user: newUser,
    };
  }

  // Forgot password route
  @Post('forgot-password')
  async forgotPassword(@Body('email') email: string): Promise<{ success: boolean; message: string }> {
    try {
      const result = await this.userService.forgotPassword(email);
      if (result.success) {
        return { success: true, message: 'Password reset link sent to your email.' };
      } else {
        return { success: false, message: result.message };
      }
    } catch (error) {
      return { success: false, message: 'Internal Server Error' };
    }
  }

  // Reset password route
  @Post('reset-password')
  async resetPassword(
    @Body('resetToken') resetToken: string,
    @Body('newPassword') newPassword: string,
  ): Promise<{ success: boolean; message: string }> {
    try {
      const result = await this.userService.resetPassword(resetToken, newPassword);
      if (result.success) {
        return { success: true, message: 'Password reset successfully.' };
      } else {
        return { success: false, message: result.message };
      }
    } catch (error) {
      return { success: false, message: 'Internal Server Error' };
    }
  }
}
