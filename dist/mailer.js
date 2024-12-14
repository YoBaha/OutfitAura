"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.MailerService = void 0;
const common_1 = require("@nestjs/common");
const nodemailer = require("nodemailer");
let MailerService = class MailerService {
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
    async sendResetCode(email, resetCode) {
        const mailOptions = {
            from: 'modesta.green58@ethereal.email',
            to: email,
            subject: 'Password Reset Code',
            text: `Your password reset code is: ${resetCode}`,
            html: `<p>Your password reset code is: <b>${resetCode}</b></p>`,
        };
        try {
            await this.transporter.sendMail(mailOptions);
            console.log('Reset code email sent to:', email);
        }
        catch (error) {
            console.error('Error sending email:', error);
            throw new Error('Error sending email');
        }
    }
};
exports.MailerService = MailerService;
exports.MailerService = MailerService = __decorate([
    (0, common_1.Injectable)(),
    __metadata("design:paramtypes", [])
], MailerService);
//# sourceMappingURL=mailer.js.map