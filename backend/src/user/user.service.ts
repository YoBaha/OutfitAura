import { Injectable, HttpException, HttpStatus } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { User } from './user.interface';
import * as bcrypt from 'bcrypt';
import { transporter } from './mailer';
import * as crypto from 'crypto';

@Injectable()
export class UserService {
  constructor(
    @InjectModel('User') private userModel: Model<User>,
  ) {}

  // Forgot password
  async forgotPassword(email: string): Promise<{ success: boolean; message: string }> {
    if (!email) {
      throw new HttpException('Email is required', HttpStatus.BAD_REQUEST);
    }

    const user = await this.userModel.findOne({ email }).exec();
    if (!user) {
      throw new HttpException('User not found', HttpStatus.NOT_FOUND);
    }

    // Check if a reset token already exists
    if (user.resetToken) {
      throw new HttpException('A password reset request is already in progress', HttpStatus.BAD_REQUEST);
    }

    // Generate a reset token
    const resetToken = crypto.randomBytes(32).toString('hex');
    user.resetToken = resetToken;
    user.resetTokenDate = new Date(); 
    await user.save();

    const resetUrl = `http://localhost:3000/reset-password/${resetToken}`; 
    const mailOptions = {
      from: 'ben.mansour789@gmail.com', 
      to: email,
      subject: 'Password Reset Request',
      text: `Click the link to reset your password: ${resetUrl}`,
    };

    try {
      await transporter.sendMail(mailOptions);
      return { success: true, message: 'Password reset email sent' };
    } catch (error) {
      console.error('Error sending email:', error);
      throw new HttpException('Error sending email', HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // Reset password
  async resetPassword(resetToken: string, newPassword: string): Promise<{ success: boolean; message: string }> {
    if (!resetToken || !newPassword) {
      throw new HttpException('Reset token and new password are required', HttpStatus.BAD_REQUEST);
    }

    const user = await this.userModel.findOne({ resetToken }).exec();
    if (!user) {
      throw new HttpException('Invalid or expired reset token', HttpStatus.BAD_REQUEST);
    }

    // Check if the reset token is expired (1 hour validity)
    if (user.resetTokenDate && Date.now() - user.resetTokenDate.getTime() > 3600000) {
      throw new HttpException('Reset token has expired', HttpStatus.BAD_REQUEST);
    }

    // Hash the new password
    const hashedPassword = await bcrypt.hash(newPassword, 10);
    user.password = hashedPassword;
    user.resetToken = null; // Clear reset token after successful reset
    user.resetTokenDate = null; // Clear reset token date
    await user.save();

    return { success: true, message: 'Password has been reset successfully' };
  }

  // Validate user credentials
  async validateUser(email: string, password: string): Promise<User | null> {
    if (!email || !password) {
      throw new HttpException('Email and password are required', HttpStatus.BAD_REQUEST);
    }

    const user = await this.userModel.findOne({ email }).exec();
    if (user && await bcrypt.compare(password, user.password)) {
      return user;
    }
    return null;
  }

  // Find user by email
  async findUserByEmail(email: string): Promise<User | null> {
    if (!email) {
      throw new HttpException('Email is required', HttpStatus.BAD_REQUEST);
    }
    return this.userModel.findOne({ email }).exec();
  }

  // Create a new user
  async createUser(email: string, password: string): Promise<User> {
    if (!email || !password) {
      throw new HttpException('Email and password are required', HttpStatus.BAD_REQUEST);
    }

    const existingUser = await this.userModel.findOne({ email }).exec();
    if (existingUser) {
      throw new HttpException('User already exists', HttpStatus.BAD_REQUEST);
    }

    const hashedPassword = await bcrypt.hash(password, 10);
    const newUser = new this.userModel({ email, password: hashedPassword });
    return newUser.save();
  }
}
