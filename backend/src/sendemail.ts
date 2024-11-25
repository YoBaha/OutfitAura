import { Controller, Post, Body } from '@nestjs/common';
import { transporter } from './user/mailer'; // Import your transporter from mailer.ts

@Controller('send-email')
export class EmailController {

  @Post()
  async sendEmail(@Body() emailRequest: { to: string, subject: string, body: string }) {
    try {
      await transporter.sendMail({
        from: 'ben.mansour789@gmail.com',
        to: emailRequest.to,
        subject: emailRequest.subject,
        text: emailRequest.body,
      });
      return { message: 'Email sent successfully' };
    } catch (error) {
      console.error(error);
      return { message: 'Failed to send email', error };
    }
  }
}
