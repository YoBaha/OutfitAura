import { IsEmail, IsNotEmpty, Length } from 'class-validator';

export class SignUpRequest {
  @IsEmail()
  @IsNotEmpty()
  email: string;

  @IsNotEmpty()
  @Length(6, 20)
  password: string;
}
