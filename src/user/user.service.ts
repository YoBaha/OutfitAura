import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import * as bcrypt from 'bcrypt';
import { User } from '../interfaces/user.interface';
import { OAuth2Client } from 'google-auth-library'; 

@Injectable()
export class UserService {
    private oAuth2Client: OAuth2Client;

    constructor(@InjectModel('User') private userModel: Model<User>) {
        // Initialize Google OAuth2Client with your Google client ID
        this.oAuth2Client = new OAuth2Client('710576853212-cikv2iv23aobn9t2qt7s2hif3k2q5c9q.apps.googleusercontent.com');
    }

    // Create a new user
    async createUser(email: string, password: string): Promise<{ success: boolean; message: string }> {
        try {
            const existingUser = await this.userModel.findOne({ email }).exec();
            if (existingUser) {
                return { success: false, message: 'User already exists' };
            }

            const hashedPassword = await bcrypt.hash(password, 10);
            const newUser = new this.userModel({ email, password: hashedPassword });
            await newUser.save();

            return { success: true, message: 'User created successfully' };
        } catch (error) {
            return { success: false, message: 'Error creating user: ' + error.message };
        }
    }

    // Validate user credentials
    async validateUser(email: string, password: string): Promise<{ success: boolean; user?: any; message?: string }> {
        try {
            const user = await this.userModel.findOne({ email }).exec();

            if (user && user.password && (await bcrypt.compare(password, user.password))) {
                return { success: true, user };
            }

            return { success: false, message: 'Invalid credentials' };
        } catch (error) {
            return { success: false, message: 'Error validating user: ' + error.message };
        }
    }

    // Handle Google Sign-In
    async handleGoogleSignIn(idToken: string): Promise<{ success: boolean; user?: any; message?: string }> {
        try {
            // Verify the ID token with Google's API
            const ticket = await this.oAuth2Client.verifyIdToken({
                idToken,
                audience: '710576853212-cikv2iv23aobn9t2qt7s2hif3k2q5c9q.apps.googleusercontent.com', // Make sure this matches your client ID
            });

            const payload = ticket.getPayload();
            const googleUserId = payload?.sub; // Google's unique user ID
            const email = payload?.email;

            // Check if the user exists in the database
            let user = await this.userModel.findOne({ email }).exec();

            if (!user) {
                // If user doesn't exist, create a new user
                user = new this.userModel({ email, password: null }); // No password for Google login
                await user.save();
            }

            return { success: true, user };
        } catch (error) {
            return { success: false, message: 'Error verifying Google token: ' + error.message };
        }
    }
}
