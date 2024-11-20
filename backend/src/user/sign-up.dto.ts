import { IsEmail, IsNotEmpty, Length } from 'class-validator';

export class SignUpRequest {
  @IsEmail()
  @IsNotEmpty()
  email: string;

  @IsNotEmpty()
  @Length(6, 20)
  password: string; // Ensure a reasonable length for the password
}

export class SignUpResponse {
  success: boolean;
  message?: string;
  user?: any;
}
