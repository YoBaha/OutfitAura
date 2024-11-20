export interface User {
    email: string;
    password: string;
    resetToken?: string; // Add resetToken property (optional)
    resetTokenDate?: Date; // Optionally, add the resetTokenDate field if needed
  }
  