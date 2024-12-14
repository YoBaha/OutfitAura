// src/mailer/mailer.service.ts
import { Injectable } from '@nestjs/common';
import * as nodemailer from 'nodemailer';

@Injectable()
export class MailerService {
  private transporter;

  constructor() {
    this.transporter = nodemailer.createTransport({
      host: 'smtp.ethereal.email',
      port: 587,
      auth: {
        user: 'murphy.homenick@ethereal.email',
        pass: 'e9pTunf7gzGz1sCX7p',
      },
    });
  }

  // Function to send reset code email
  async sendResetCode(email: string, resetCode: string): Promise<void> {
    const mailOptions = {
      from: 'modesta.green58@ethereal.email', // Sender's email
      to: email, 
      subject: 'Password Reset Code',
      text: `Your password reset code is: ${resetCode}`, // Plain text version
      html: `<p>Your password reset code is: <b>${resetCode}</b></p>`, // HTML version
    };

    try {
      await this.transporter.sendMail(mailOptions);
      console.log('Reset code email sent to:', email);
    } catch (error) {
      console.error('Error sending email:', error);
      throw new Error('Error sending email');
    }
  }
}
