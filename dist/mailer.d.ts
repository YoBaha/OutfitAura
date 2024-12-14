export declare class MailerService {
    private transporter;
    constructor();
    sendResetCode(email: string, resetCode: string): Promise<void>;
}
