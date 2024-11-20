import * as nodemailer from 'nodemailer';

export const transporter = nodemailer.createTransport({
  host: 'smtp.elasticemail.com',
  port: 2525, //2525
  secure: false,
  auth: {
    user: 'ben.mansour789@gmail.com',
    pass: '1095F1527A2B6834F2F3DE24AF64EC681EAC',
  },
  socketTimeout: 30000, // Timeout after 30 seconds
});
